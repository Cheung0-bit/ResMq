package io.github.resmq.core.client;

/**
 * <The producer publishes the confirmation interface>
 *
 * @author zhanglin
 */
public interface SendCallback {
    /**
     * send success
     * @param sendResult
     */
    void onSuccess(final SendResult sendResult);

    /**
     * send fail
     * @param e
     */
    void onException(final Throwable e);
}
