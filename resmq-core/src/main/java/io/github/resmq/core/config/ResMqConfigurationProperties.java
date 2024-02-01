package io.github.resmq.core.config;

/**
 * <简单主题分组参数>
 *
 * @Author zhanglin
 * @createTime 2024/1/30 16:00
 */
public class ResMqConfigurationProperties {

    /** 消息主题，必填 */
    private String topic;
    /** 消费者组，使用@ResMqListener时（接收消息），必填 */
    private String group;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
