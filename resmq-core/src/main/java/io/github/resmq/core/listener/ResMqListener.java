package io.github.resmq.core.listener;

import io.github.resmq.core.exception.ResMqException;

/**
 * <Consumer listener interface>
 *
 * @author zhanglin
 */
public interface ResMqListener<T> {

    /**
     * Listening for message entities
     * @param message
     */
    void receiveMessage(T message) throws ResMqException;

}
