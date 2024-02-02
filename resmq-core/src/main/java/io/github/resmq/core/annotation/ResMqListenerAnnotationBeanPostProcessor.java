package io.github.resmq.core.annotation;

import com.alibaba.fastjson.JSON;
import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.core.constant.Constants;
import io.github.resmq.core.exception.ResMqException;
import io.github.resmq.core.listener.ResMqListenerParam;
import io.github.resmq.core.util.SpringUtil;
import io.github.resmq.core.config.ResMqConfigurationProperties;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <Bean post process>
 *
 * @author zhanglin
 */
public class ResMqListenerAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final List<ListenerMethod> listenerMethods = new ArrayList<>();

    private final List<ResMqListenerParam> resMqListenerParamList = new ArrayList<>();

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        // 使用该工具类 为了处理可能存在的代理对象
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        final List<ListenerMethod> methods = new ArrayList<>();
        ReflectionUtils.doWithMethods(targetClass, method -> {
            Collection<ResMqListener> listenerAnnotations = findListenerAnnotations(method);
            if (!listenerAnnotations.isEmpty()) {
                methods.add(new ListenerMethod(bean, method, listenerAnnotations.iterator().next()));
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);

        if (!methods.isEmpty()) {
            listenerMethods.addAll(methods);
        }
        return bean;
    }

    // Find the ResMqListener annotation on the given element (AnnotatedElement)
    private Collection<ResMqListener> findListenerAnnotations(AnnotatedElement element) {
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).stream(ResMqListener.class).map(MergedAnnotation::synthesize)
                .collect(Collectors.toList());
    }

    /**
     * 处理监听器参数
     */
    public void handleListenerParameters(StringRedisTemplate stringRedisTemplate) {
        ResMqProperties resMqProperties = SpringUtil.getBean(ResMqProperties.class);
        stringRedisTemplate.opsForValue().set(Constants.CONFIGURATION_KEY, JSON.toJSONString(resMqProperties));
        for (ListenerMethod method : listenerMethods) {
            buildMqListenerParam(method.bean, method, method.resMqListener, resMqProperties);
        }
        listenerMethods.clear();
    }

    private void buildMqListenerParam(Object bean, ListenerMethod listenerMethod, ResMqListener resMqListener, ResMqProperties resMqProperties) {
        // The business name of the message
        String name = resMqListener.name();
        // 消息主题
        String topic = resMqListener.topic();
        // 消费者组
        String group = resMqListener.group();
        boolean nameFlag = StringUtils.hasText(name);
        boolean topicFlag = StringUtils.hasText(topic);
        boolean groupFlag = StringUtils.hasText(group);
        // 1.Setting message subject, consumer group annotation direct configuration has higher priority
        if (topicFlag && groupFlag) {
            ResMqListenerParam resMqListenerParam = new ResMqListenerParam();
            resMqListenerParam.setTopic(Constants.TOPIC_PREFIX + topic).setGroup(group).setBean(bean).setMethod(listenerMethod.method);
            resMqListenerParamList.add(resMqListenerParam);
        } else if (nameFlag) {
            // 2.Set the message service name
            ResMqConfigurationProperties resMqConfigurationProperties = resMqProperties.getStreams().get(name);
            Assert.isTrue(null != resMqConfigurationProperties, "@ResMqListener attribute name is [" + name + "], not found in the configuration [res-mq.streams." + name + "],[" + bean.getClass().getName() + "],[" + listenerMethod.method.getName() + "]");
            topic = resMqConfigurationProperties.getTopic();
            group = resMqConfigurationProperties.getGroup();
            Assert.isTrue(StringUtils.hasText(topic), "Please specific [topic] under [res-mq.streams." + name + "] configuration.");
            Assert.isTrue(StringUtils.hasText(group), "Please specific [group] under [res-mq.streams." + name + "] configuration.");
            ResMqListenerParam resMqListenerParam = new ResMqListenerParam();
            resMqListenerParam.setTopic(Constants.TOPIC_PREFIX + topic).setGroup(group).setBean(bean).setMethod(listenerMethod.method);
            resMqListenerParamList.add(resMqListenerParam);
        } else {
            // In @ResMqListener, only one topic or group is set
            throw new ResMqException("Please specific [topic] and [group] under @MqListener,[" + bean.getClass().getName() + "],[" + listenerMethod.method.getName() + "]");
        }

    }

    public List<ResMqListenerParam> getResMqListenerParamList() {
        return resMqListenerParamList;
    }

    private static class ListenerMethod {

        final Object bean;
        final Method method;
        final ResMqListener resMqListener;

        ListenerMethod(Object bean, Method method, ResMqListener resMqListener) {
            this.bean = bean;
            this.method = method;
            this.resMqListener = resMqListener;
        }

    }

}
