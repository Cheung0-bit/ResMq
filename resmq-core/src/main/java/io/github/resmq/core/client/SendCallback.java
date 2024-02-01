package io.github.resmq.core.client;

/**
 * <生产者发布确认接口>
 *
 * @Author zhanglin
 * @createTime 2024/1/29 18:52
 */
public interface SendCallback {
    /**
     * 发送成功
     * @param sendResult
     */
    void onSuccess(final SendResult sendResult);

    /**
     * 发送失败
     * @param e
     */
    void onException(final Throwable e);
}
