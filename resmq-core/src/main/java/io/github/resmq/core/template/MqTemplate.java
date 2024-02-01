package io.github.resmq.core.template;

import com.alibaba.fastjson.JSON;
import io.github.resmq.core.client.SendCallback;
import io.github.resmq.core.exception.ResMqException;

import java.util.concurrent.TimeUnit;

/**
 * <消息队列模板抽象>
 *
 * @Author zhanglin
 * @createTime 2024/1/29 18:24
 */
public abstract class MqTemplate {

    /**
     * 任意类型对象序列化为JSON字符串
     *
     * @param message
     * @return
     */
    public String toJsonStr(Object message) {
        return JSON.toJSONString(message);
    }

    /**
     * 同步发送消息
     *
     * @param topic   主题
     * @param message 消息
     * @return
     */
    public abstract boolean syncSend(String topic, Object message);

    /**
     * 同步发送延迟消息
     *
     * @param topic     主题
     * @param message   消息
     * @param delayTime 延迟时间
     * @param timeUnit  延时时间单位
     * @return
     */
    public boolean syncDelaySend(String topic, Object message, int delayTime, TimeUnit timeUnit) {
        throw new ResMqException("未实现同步延迟消息发送");
    }

    /**
     * 异步发送消息
     *
     * @param topic
     * @param message
     * @param sendCallback
     */
    public void asyncSend(String topic, Object message, SendCallback sendCallback) {
        throw new ResMqException("未实现异步消息发送");
    }
}
