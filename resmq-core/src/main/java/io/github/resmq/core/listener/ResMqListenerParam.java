package io.github.resmq.core.listener;

import java.lang.reflect.Method;

/**
 * <消息队列监听参数>
 *
 * @Author zhanglin
 * @createTime 2024/1/29 18:18
 */
public class ResMqListenerParam {

    private String topic;

    private String group;

    /**
     * 注解@ResMqListener监听的Bean
     */
    private Object bean;
    /**
     * 注解@ResMqListener监听的Bean的方法
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
