package io.github.resmq.core.template;

import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.core.constant.Constants;
import io.github.resmq.core.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <Redis Stream消息队列模板实现>
 *
 * @Author zhanglin
 * @createTime 2024/1/29 19:06
 */
public class ResMqTemplate extends MqTemplate {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final StringRedisTemplate stringRedisTemplate;

    public ResMqTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 发送消息
     * xadd key MAXLEN ~ 1000 ID field string [field string ...]
     */
    private static final RedisScript<String> SCRIPT_MESSAGE =
            new DefaultRedisScript<>(
                    // 投递消息到队列
                    "return redis.call('xadd', KEYS[1], 'MAXLEN', '~', ARGV[1], '*', 'message', ARGV[2])"
                    , String.class
            );

//    /**
//     * 延迟队列 EX：秒，PX：毫秒
//     * <p>
//     * hset key field value 先将延迟消息存储到Hash结构中，方便后面取出
//     * </p>
//     * <p>
//     * set key value [expiration EX seconds|PX milliseconds] [NX|XX] 添加倒计时KEY key到期后触发过期监听器
//     * </p>
//     */
//    private static final RedisScript<String> SCRIPT_DELAY_MESSAGE =
//            new DefaultRedisScript<>(
//                    "if redis.call('hset', KEYS[2], ARGV[3], ARGV[4]) == 1 then" +
//                            "    return redis.call('set', KEYS[1], ARGV[1], 'NX', 'PX', ARGV[2])" +
//                            "else" +
//                            "    return 'fail' " +
//                            "end", String.class
//            );

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
     * @param topic     主题
     * @param message   消息
     * @param delayTime 延迟时间
     * @param timeUnit  延时时间单位
     * @return
     */
    @Override
    public boolean syncDelaySend(String topic, Object message, int delayTime, TimeUnit timeUnit) {
        //todo 延迟队列修正
        String str = toJsonStr(message);
        try {
            String key = Constants.DELAY_MESSAGE_TTL_PREFIX_KEY + topic;
            // 占位
            stringRedisTemplate.opsForZSet().addIfAbsent(key, "placeholde", 0);
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
