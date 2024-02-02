package io.github.resmq.core.listener;

import java.lang.reflect.Method;

/**
 * <Message queue listen parameters>
 *
 * @author zhanglin
 */
public class ResMqListenerParam {

    private String topic;

    private String group;

    /**
     * Annotate @ResMqListener listen to the Bean
     */
    private Object bean;
    /**
     * Annotate @ResMqListener methods on beans to listen to
     */
    private Method method;

    public String getTopic() {
        return topic;
    }

    public ResMqListenerParam setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public ResMqListenerParam setGroup(String group) {
        this.group = group;
        return this;
    }

    public Object getBean() {
        return bean;
    }

    public ResMqListenerParam setBean(Object bean) {
        this.bean = bean;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public ResMqListenerParam setMethod(Method method) {
        this.method = method;
        return this;
    }
}
