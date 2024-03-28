package io.github.resmq.core.task;

import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.core.constant.Constants;
import io.github.resmq.core.util.LuaScriptUtil;
import io.github.resmq.core.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <Handle dead messages at regular intervals>
 *
 * @author zhanglin
 */
public class ResMqDeadMessageTasker {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ScheduledExecutorService timer;
    private final StringRedisTemplate stringRedisTemplate;
    /**
     * 1 ACK unACK messages in the Pending queue
     * 2 It is delivered to the dead message queue for subsequent processing by the consumer
     */
    private static final RedisScript<String> SCRIPT_DEAD_MESSAGE =
            new DefaultRedisScript<>(
                    // ACK消息
                    "local ack = redis.call('xack', KEYS[1], ARGV[1], ARGV[2]) " +
                            "if ack==1 then" +
                            "    return redis.call('xadd', KEYS[2], 'MAXLEN', '~', ARGV[3], '*', 'message', ARGV[4]) " +
                            "else " +
                            "    return 0 " +
                            "end"
                    , String.class
            );
    /**
     * Topics to listen for
     */
    private final Set<String> topics;
    /**
     * Judged as dead letter: number of message consumption
     */
    private final long deadMessageDeliveryCount;
    /**
     * Judged dead letter: message consumption time unit in seconds
     */
    private final long deadMessageDeliverySecond;

    /**
     * @param stringRedisTemplate
     * @param topics
     * @param deadMessageDeliveryCount
     * @param deadMessageDeliverySecond
     */
    public ResMqDeadMessageTasker(StringRedisTemplate stringRedisTemplate, Set<String> topics, long deadMessageDeliveryCount, long deadMessageDeliverySecond) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.topics = topics;
        this.deadMessageDeliveryCount = deadMessageDeliveryCount <= 0 ? 1 : deadMessageDeliveryCount;
        this.deadMessageDeliverySecond = deadMessageDeliverySecond <= 0 ? 60 : deadMessageDeliverySecond;
    }

    public void start() {
        ResMqProperties resMqProperties = SpringUtil.getBean(ResMqProperties.class);
        if (timer == null) {
            timer = new ScheduledThreadPoolExecutor(resMqProperties.getDeadMessageScheduledThreadPoolCoreSize()
                    , new ThreadFactory() {
                private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
                private final AtomicInteger threadNumber = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = defaultFactory.newThread(r);
                    // 设置为守护线程
                    if (!thread.isDaemon()) {
                        thread.setDaemon(true);
                    }
                    thread.setName("ResMq-Dead-Message-" + threadNumber.getAndIncrement());
                    return thread;
                }
            });
        }
        timer.scheduleWithFixedDelay(new DeadMessageTasker(), resMqProperties.getDeadMessageTimerInitialDelay()
                , resMqProperties.getDeadMessageTimerDelay(), TimeUnit.SECONDS);
    }

    public void stop() {
        if (timer != null) {
            timer.shutdown();
        }
    }

    class DeadMessageTasker extends TimerTask {
        @Override
        public void run() {
            try {
                StreamOperations<String, String, String> streamOperations = stringRedisTemplate.opsForStream();
                for (String topic : topics) {
                    if (topic.contains("DLQ")) {
                        continue;
                    }
                    // Obtaining groups of consumers
                    StreamInfo.XInfoGroups groups = LuaScriptUtil.getInfoGroups(topic, stringRedisTemplate);
                    if (groups == null) {
                        continue;
                    }
                    for (StreamInfo.XInfoGroup group : groups) {
                        Long pendingCount = group.pendingCount();// unAcked Messages
                        if (pendingCount > 0) {
                            String groupName = group.groupName();
                            // Dead message queue topic
                            String deadTopic = topic + ":DLQ:" + groupName;
                            // Get pending messages in the consumer group
                            PendingMessagesSummary pendingMessagesSummary = LuaScriptUtil.getPendingMessagesSummary(topic, groupName, stringRedisTemplate);
                            // Number of pending messages per consumer
                            Map<String, Long> pendingMessagesPerConsumer = null;
                            if (pendingMessagesSummary != null) {
                                pendingMessagesPerConsumer = pendingMessagesSummary.getPendingMessagesPerConsumer();
                                pendingMessagesPerConsumer.forEach((consumer, value) -> {
                                    // Number of pending messages for consumers
                                    long consumerTotalPendingMessages = value;
                                    if (consumerTotalPendingMessages > 0) {
                                        // Read the first 10 records from the consumer's pending queue, starting with the record with ID=0 and working up to the maximum ID, processing 10 at a time
                                        PendingMessages pendingMessages = LuaScriptUtil.getPendingMessages(topic, groupName, consumer, Range.closed("0", "+"), SpringUtil.getBean(ResMqProperties.class).getPendingMessagesPullCount(), stringRedisTemplate);
                                        // Iterate over the details of all Pending messages
                                        pendingMessages.forEach(message ->
                                                // ID of the message
                                                executeDeadMessage(streamOperations, topic, groupName, deadTopic, message)
                                        );
                                    }
                                });
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void executeDeadMessage(StreamOperations<String, String, String> streamOperations, String topic, String groupName, String deadTopic,
                                        PendingMessage message) {
            String flag = "-";
            String messageId = message.getIdAsString();
            // The time consumed for a message delivery
            Duration elapsedTimeSinceLastDelivery = message.getElapsedTimeSinceLastDelivery();
            // The number of deliveries in multiple consumers
            long deliveryCount = message.getTotalDeliveryCount();
            String consumerName = message.getConsumerName();
            // Whether the message is dead letter or not
            if (deliveryCount > deadMessageDeliveryCount || elapsedTimeSinceLastDelivery.getSeconds() > deadMessageDeliverySecond) {
                // 获取消息内容
                List<MapRecord<String, String, String>> result = streamOperations
                        .range(topic, Range.closed(messageId, messageId));
                if (result != null && !result.isEmpty()) {
                    MapRecord<String, String, String> mapRecord = result.get(0);
                    logger.warn("Dead Message: topic={}, group={}, consumer={}, id={}, deliveryCount={}, deliveryTimer={}",
                            topic, groupName, consumerName, messageId, deliveryCount, elapsedTimeSinceLastDelivery.getSeconds());
                    String msg = mapRecord.getValue().values().iterator().next();
                    // ack and put into the dead message queue
                    // Lua
                    Object deadMessageFlag = stringRedisTemplate.execute(
                            SCRIPT_DEAD_MESSAGE,
                            stringRedisTemplate.getStringSerializer(),
                            stringRedisTemplate.getStringSerializer(),
                            Arrays.asList(topic, deadTopic),
                            groupName, messageId,
                            String.valueOf(SpringUtil.getBean(ResMqProperties.class).getMaxQueueSize()), msg
                    );
                    // Check if it was added to the dead message queue
                    if (deadMessageFlag != null && deadMessageFlag.toString().contains(flag)) {
                        // 计数器 todo 原子性
                        long timestamp = System.currentTimeMillis() / (24 * 60 * 60 * 1000);
                        String key = Constants.MESSAGE_COUNT_KEY + timestamp + ":" + topic.substring(Constants.TOPIC_PREFIX.length());
                        stringRedisTemplate.opsForHash().increment(key, "dlq-count", 1);
                        logger.info("Dead Message ok->topic:{}, id={}", deadTopic, deadMessageFlag);
                    } else {
                        logger.error("Dead Message ok->topic:{}, id={}", deadTopic, deadMessageFlag);
                    }
                }
            }
        }
    }
}
