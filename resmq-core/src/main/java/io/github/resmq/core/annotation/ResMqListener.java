package io.github.resmq.core.annotation;

import java.lang.annotation.*;

/**
 * 消息监听
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface ResMqListener {
    /** 消息业务名称 */
    String name() default "";
    /** 设置消费者数量 */
    int concurrentConsumers() default 1;
    /** 主题topic */
    String topic() default "";
    /** 消费分组 */
    String group() default "";
}
