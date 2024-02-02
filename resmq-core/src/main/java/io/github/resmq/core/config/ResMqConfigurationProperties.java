package io.github.resmq.core.config;

/**
 * <Simple topic grouping parameter>
 *
 * @author zhanglin
 */
public class ResMqConfigurationProperties {

    /** Message subject, required */
    private String topic;
    /** Consumer group, when using @ResMqListener (to receive messages), required */
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
