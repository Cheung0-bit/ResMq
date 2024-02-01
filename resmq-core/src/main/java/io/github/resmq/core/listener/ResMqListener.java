package io.github.resmq.core.listener;

import io.github.resmq.core.exception.ResMqException;

/**
 * <消费者监听器接口>
 *
 * @Author zhanglin
 * @createTime 2024/1/29 18:04
 */
public interface ResMqListener<T> {

    /**
     * 监听消息实体
     * @param message
     * @throws ResMqException
     */
    void receiveMessage(T message) throws ResMqException;

}
