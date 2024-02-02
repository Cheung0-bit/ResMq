package io.github.resmq.core.template;

import com.alibaba.fastjson.JSON;
import io.github.resmq.core.client.SendCallback;
import io.github.resmq.core.exception.ResMqException;

import java.util.concurrent.TimeUnit;

/**
 * <Message queue template abstraction>
 *
 * @author zhanglin
 */
public abstract class MqTemplate {

    /**
     * Serialize an object of any type into a JSON string
     *
     * @param message
     * @return
     */
    public String toJsonStr(Object message) {
        return JSON.toJSONString(message);
    }

    /**
     * Sending messages synchronously
     *
     * @param topic topic
     * @param message message
     * @return
     */
    public abstract boolean syncSend(String topic, Object message);

    /**
     * Send delayed messages synchronously
     *
     * @param topic     topic
     * @param message   message
     * @param delayTime delayTime
     * @param timeUnit  timeUnit
     * @return
     */
    public boolean syncDelaySend(String topic, Object message, int delayTime, TimeUnit timeUnit) {
        throw new ResMqException("Synchronous delayed message delivery is not implemented");
    }

    /**
     * Sending messages asynchronously
     *
     * @param topic
     * @param message
     * @param sendCallback
     */
    public void asyncSend(String topic, Object message, SendCallback sendCallback) {
        throw new ResMqException("Asynchronous message sending is not implemented");
    }
}
