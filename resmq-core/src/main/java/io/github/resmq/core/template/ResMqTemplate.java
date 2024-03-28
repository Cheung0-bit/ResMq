package io.github.resmq.core.template;

import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.core.constant.Constants;
import io.github.resmq.core.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <Redis Stream消息队列模板实现>
 *
 * @author zhanglin
 */
public class ResMqTemplate extends MqTemplate {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final StringRedisTemplate stringRedisTemplate;

    public ResMqTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * send message
     * xadd key MAXLEN ~ 1000 ID field string [field string ...]
     */
    private static final RedisScript<String> SCRIPT_MESSAGE =
            new DefaultRedisScript<>(
                    // 投递消息到队列
                    "return redis.call('xadd', KEYS[1], 'MAXLEN', '~', ARGV[1], '*', 'message', ARGV[2])"
                    , String.class
            );

    @Override
    public boolean syncSend(String topic, Object message) {
        String str = null;
        try {
            if (!(message instanceof String)) {
                str = toJsonStr(message);
            } else {
                str = message.toString();
            }
            // topic预处理
            topic = Constants.TOPIC_PREFIX + topic;
            Object deliveryId = stringRedisTemplate.execute(
                    SCRIPT_MESSAGE,
                    stringRedisTemplate.getStringSerializer(),
                    stringRedisTemplate.getStringSerializer(),
                    Collections.singletonList(topic),
                    String.valueOf(SpringUtil.getBean(ResMqProperties.class).getMaxQueueSize()), str
            );
            logger.debug("Sync Send Success: topic=[{}], message=[{}], offset=[{}]", topic.substring(Constants.TOPIC_PREFIX.length()), str, deliveryId);
            // 计数器 todo 原子性
            long timestamp = System.currentTimeMillis() / (24 * 60 * 60 * 1000);
            Long size = stringRedisTemplate.opsForSet().size(Constants.GROUP_COUNT_KEY + topic.substring(Constants.TOPIC_PREFIX.length()));
            Assert.isTrue(size != null, "Why Group Count is Zero?");
            String key = Constants.MESSAGE_COUNT_KEY + timestamp + ":" + topic.substring(Constants.TOPIC_PREFIX.length());
            stringRedisTemplate.opsForHash().increment(key, "total-count", size);
            return true;
        } catch (Exception e) {
            logger.error("Sync Send Error: " + topic + ", " + str, e.getMessage());
        }
        return false;
    }

    /**
     * 采用Zset实现延迟发送
     * zadd key [NX|XX] [CH] [INCR] score member [score member ...]
     *
     * @param topic     topic
     * @param message   message
     * @param delayTime delayTime
     * @param timeUnit  timeUnit
     * @return
     */
    @Override
    public boolean syncDelaySend(String topic, Object message, int delayTime, TimeUnit timeUnit) {
        String str = toJsonStr(message);
        try {
            String key = Constants.DELAY_MESSAGE_TTL_PREFIX_KEY + topic;
            // 占位
            stringRedisTemplate.opsForZSet().addIfAbsent(key, "placeholder", 0);
            long timestamp = timeUnit.toSeconds(delayTime);
            long now = System.currentTimeMillis() / 1000;
            timestamp += now;
            Boolean isSuccess = stringRedisTemplate.opsForZSet().addIfAbsent(key, str, timestamp);
            if (isSuccess != null && isSuccess.equals(Boolean.TRUE)) {
                logger.debug("Sync Delay Send Success: topic=[{}], message=[{}], delayTime=[{}], timeUnit=[{}]", topic, str, delayTime, timeUnit);
                return true;
            } else {
                logger.warn("Sync Delay Send Fail (Repeated Message): topic=[{}], message=[{}], delayTime=[{}], timeUnit=[{}]", topic, str, delayTime, timeUnit);
            }
        } catch (Exception e) {
            logger.error("Sync Delay Send Error: topic=[" + topic + "],message=[" + str + "],delayTime=[" + delayTime + "],timeUnit=[" + timeUnit + "]", e.getMessage());
        }
        return false;
    }
}
