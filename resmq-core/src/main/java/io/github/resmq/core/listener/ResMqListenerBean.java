package io.github.resmq.core.listener;

import io.github.resmq.core.exception.ResMqException;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;

import java.lang.reflect.Method;

/**
 * <Redis Mq 监听实现>
 *
 * @Author zhanglin
 * @createTime 2024/1/30 15:28
 */
public class ResMqListenerBean extends ResMqListenerAdapter<String> implements StreamListener<String, MapRecord<String, String, String>> {

    private final StringRedisTemplate stringRedisTemplate;
    /**
     * 主题
     */
    private final String destination;
    /**
     * 消费组
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
        } catch (Exception e) {
            throw new ResMqException(e.getMessage());
        }
    }
}
