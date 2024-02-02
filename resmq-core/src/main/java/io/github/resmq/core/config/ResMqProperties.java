package io.github.resmq.core.config;

import java.util.HashMap;
import java.util.Map;

/**
 * <User-defined parameters>
 *
 * @author zhanglin
 */
public class ResMqProperties {

    /**
     * Enabled or not. The default value is true
     */
    private boolean enable = true;

    /**
     * Message queue configuration, key: business name
     */
    private Map<String, ResMqConfigurationProperties> streams = new HashMap<>();

    /**
     * stream backlog threshold
     */
    private int maxQueueSize = 1000;

    /**
     * Dead-letter message transfer threshold
     */
    private long deadMessageDeliveryCount = 1;

    /**
     * Dead letter message time threshold
     */
    private long deadMessageDeliverySecond = 60;

    /**
     * Dead letter processing thread pool number of core threads
     */
    private int deadMessageScheduledThreadPoolCoreSize = 2;

    /**
     * Dead-letter task triggering initial time
     */
    private long deadMessageTimerInitialDelay = 30;

    /**
     * Dead letter task interval time
     */
    private long deadMessageTimerDelay = 30;

    /**
     * The number of pending list messages pulled each time
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
