package io.github.resmq.core.listener;

import com.alibaba.fastjson.JSON;
import io.github.resmq.core.exception.ResMqException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <监听消息队列适配器>
 *
 * @Author zhanglin
 * @createTime 2024/1/29 18:12
 */
public class ResMqListenerAdapter<T> implements ResMqListener<T> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Method method;
    private Object bean;

    public ResMqListenerAdapter(Method method, Object bean) {
        this.method = method;
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    @Override
    public void receiveMessage(T message) throws ResMqException {
        try {
            int parameterCount = method.getParameterCount();
            if (parameterCount != 1) {
                throw new ResMqException("The number of @ResMqListener listening method parameters must be 1");
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> clazz = parameterTypes[0];
            if (clazz == String.class) {
                method.invoke(bean, message);
            } else {
                // 自定义类型转换
                Object obj = JSON.parseObject(message.toString(), clazz);
                method.invoke(bean, obj);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ResMqException(e.getMessage());
        }
    }
}
