package io.github.resmq.core.listener;

import io.github.resmq.core.constant.Constants;
import io.github.resmq.core.exception.ResMqException;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

import java.lang.reflect.Method;

/**
 * <Redis Mq 监听实现>
 *
 * @author zhanglin
 */
public class ResMqListenerBean extends ResMqListenerAdapter<String> implements StreamListener<String, MapRecord<String, String, String>> {

    private final StringRedisTemplate stringRedisTemplate;
    /**
     * topic
     */
    private final String destination;
    /**
     * Consumer Group
     */
    private final String group;

    public ResMqListenerBean(Method method, Object bean, StringRedisTemplate stringRedisTemplate, String destination, String group) {
        super(method, bean);
        this.stringRedisTemplate = stringRedisTemplate;
        this.destination = destination;
        this.group = group;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        RecordId recordId = message.getId();
        try {
            super.receiveMessage(message.getValue().get("message"));
            // 出现异常 就会ACK失败 待加入死信队列
            stringRedisTemplate.opsForStream().acknowledge(destination, group, recordId);
            // 计数器 todo 原子性
            long timestamp = System.currentTimeMillis() / (24 * 60 * 60 * 1000);
            String key = Constants.MESSAGE_COUNT_KEY + timestamp + ":" + destination.substring(Constants.TOPIC_PREFIX.length());
            stringRedisTemplate.opsForHash().increment(key, "ack-count", 1);
        } catch (Exception e) {
            throw new ResMqException(e.getMessage());
        }
    }
}
