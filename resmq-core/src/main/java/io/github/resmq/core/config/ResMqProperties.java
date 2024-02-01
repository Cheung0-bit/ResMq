package io.github.resmq.core.config;

import java.util.HashMap;
import java.util.Map;

/**
 * <用户自定义参数>
 *
 * @Author zhanglin
 * @createTime 2024/1/30 15:56
 */
public class ResMqProperties {

    /**
     * 是否开启，默认为 true 开启
     */
    private boolean enable = true;

    /**
     * 消息队列配置，key：业务名称
     */
    private Map<String, ResMqConfigurationProperties> streams = new HashMap<>();

    /**
     * stream积压阈值
     */
    private int maxQueueSize = 1000;

    /**
     * 死信消息转移阈值
     */
    private long deadMessageDeliveryCount = 1;

    /**
     * 死信消息时间阈值
     */
    private long deadMessageDeliverySecond = 60;

    /**
     * 死信处理线程池核心线程数
     */
    private int deadMessageScheduledThreadPoolCoreSize = 2;

    /**
     * 死信任务触发初始时间
     */
    private long deadMessageTimerInitialDelay = 30;

    /**
     * 死信任务间隔时间
     */
    private long deadMessageTimerDelay = 30;

    /**
     * 每次pending列表消息拉取个数
     */
    private long pendingMessagesPullCount = 10;

    public long getPendingMessagesPullCount() {
        return pendingMessagesPullCount;
    }

    public void setPendingMessagesPullCount(long pendingMessagesPullCount) {
        this.pendingMessagesPullCount = pendingMessagesPullCount;
    }

    public long getDeadMessageTimerInitialDelay() {
        return deadMessageTimerInitialDelay;
    }

    public void setDeadMessageTimerInitialDelay(long deadMessageTimerInitialDelay) {
        this.deadMessageTimerInitialDelay = deadMessageTimerInitialDelay;
    }

    public long getDeadMessageTimerDelay() {
        return deadMessageTimerDelay;
    }

    public void setDeadMessageTimerDelay(long deadMessageTimerDelay) {
        this.deadMessageTimerDelay = deadMessageTimerDelay;
    }

    public int getDeadMessageScheduledThreadPoolCoreSize() {
        return deadMessageScheduledThreadPoolCoreSize;
    }

    public void setDeadMessageScheduledThreadPoolCoreSize(int deadMessageScheduledThreadPoolCoreSize) {
        this.deadMessageScheduledThreadPoolCoreSize = deadMessageScheduledThreadPoolCoreSize;
    }

    public long getDeadMessageDeliveryCount() {
        return deadMessageDeliveryCount;
    }

    public void setDeadMessageDeliveryCount(long deadMessageDeliveryCount) {
        this.deadMessageDeliveryCount = deadMessageDeliveryCount;
    }

    public long getDeadMessageDeliverySecond() {
        return deadMessageDeliverySecond;
    }

    public void setDeadMessageDeliverySecond(long deadMessageDeliverySecond) {
        this.deadMessageDeliverySecond = deadMessageDeliverySecond;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Map<String, ResMqConfigurationProperties> getStreams() {
        return streams;
    }

    public void setStreams(Map<String, ResMqConfigurationProperties> streams) {
        this.streams = streams;
    }
}
