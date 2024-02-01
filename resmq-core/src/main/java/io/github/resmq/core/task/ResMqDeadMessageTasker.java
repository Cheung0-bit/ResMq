package io.github.resmq.core.task;

import io.github.resmq.core.config.ResMqProperties;
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

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <死信定时处理>
 *
 * @Author zhanglin
 * @createTime 2024/1/30 14:17
 */
public class ResMqDeadMessageTasker {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ScheduledExecutorService timer;
    private final StringRedisTemplate stringRedisTemplate;
    /**
     * 1 ACK Pending队列中的已读取未ACK消息
     * 2 投递到死信队列中 等待消费者后续处理
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
     * 监听的主题
     */
    private final Set<String> topics;
    /**
     * 判断为死信：消息消费次数
     */
    private final long deadMessageDeliveryCount;
    /**
     * 判断为死信：消息消费时间单位秒
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
                    // 获取消费者组
                    StreamInfo.XInfoGroups groups = LuaScriptUtil.getInfoGroups(topic, stringRedisTemplate);
                    if (groups == null) {
                        continue;
                    }
                    for (StreamInfo.XInfoGroup group : groups) {
                        Long pendingCount = group.pendingCount();// 未ack的消息
                        if (pendingCount > 0) {
                            String groupName = group.groupName();
                            // 死信队列topic
                            // todo: 死信队列测试
                            String deadTopic = topic + ":DLQ:" + groupName;
                            // 获取消费者组里的pending消息
                            PendingMessagesSummary pendingMessagesSummary = LuaScriptUtil.getPendingMessagesSummary(topic, groupName, stringRedisTemplate);
                            // 每个消费者的pending消息数量
                            Map<String, Long> pendingMessagesPerConsumer = null;
                            if (pendingMessagesSummary != null) {
                                pendingMessagesPerConsumer = pendingMessagesSummary.getPendingMessagesPerConsumer();
                                pendingMessagesPerConsumer.forEach((consumer, value) -> {
                                    // 消费者的pending消息数量
                                    long consumerTotalPendingMessages = value;
                                    if (consumerTotalPendingMessages > 0) {
                                        // 读取消费者pending队列的前10条记录，从ID=0的记录开始，一直到ID最大值，一次处理10条
                                        PendingMessages pendingMessages = LuaScriptUtil.getPendingMessages(topic, groupName, consumer, Range.closed("0", "+"), SpringUtil.getBean(ResMqProperties.class).getPendingMessagesPullCount(), stringRedisTemplate);
                                        // 遍历所有Pending消息的详情
                                        pendingMessages.forEach(message ->
                                                // 消息的ID
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
            // 消息投递一次消费的时间
            Duration elapsedTimeSinceLastDelivery = message.getElapsedTimeSinceLastDelivery();
            // 在多个消费者中投递的次数
            long deliveryCount = message.getTotalDeliveryCount();
            String consumerName = message.getConsumerName();
            // 是否死信消息
            if (deliveryCount > deadMessageDeliveryCount || elapsedTimeSinceLastDelivery.getSeconds() > deadMessageDeliverySecond) {
                // 获取消息内容
                List<MapRecord<String, String, String>> result = streamOperations
                        .range(topic, Range.closed(messageId, messageId));
                if (result != null && !result.isEmpty()) {
                    MapRecord<String, String, String> mapRecord = result.get(0);
                    logger.warn("Dead Message: topic={}, group={}, consumer={}, id={}, deliveryCount={}, deliveryTimer={}",
                            topic, groupName, consumerName, messageId, deliveryCount, elapsedTimeSinceLastDelivery.getSeconds());
                    String msg = mapRecord.getValue().values().iterator().next();
                    // ack并放入死信队列
                    // Lua
                    Object deadMessageFlag = stringRedisTemplate.execute(
                            SCRIPT_DEAD_MESSAGE,
                            stringRedisTemplate.getStringSerializer(),
                            stringRedisTemplate.getStringSerializer(),
                            Arrays.asList(topic, deadTopic),
                            groupName, messageId,
                            String.valueOf(SpringUtil.getBean(ResMqProperties.class).getMaxQueueSize()), msg
                    );
                    // 判断是否添加到了死信队列中
                    if (deadMessageFlag != null && deadMessageFlag.toString().contains(flag)) {
                        logger.info("Dead Message ok->topic:{}, id={}", deadTopic, deadMessageFlag);
                    } else {
                        logger.error("Dead Message ok->topic:{}, id={}", deadTopic, deadMessageFlag);
                    }
                }
            }
        }
    }
}
