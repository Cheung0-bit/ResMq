package io.github.resmq.core.annotation;

import java.lang.annotation.*;

/**
 * Message Listening
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface ResMqListener {
    /** message service name */
    String name() default "";
    /** set the number of consumers */
    int concurrentConsumers() default 1;
    /** topic */
    String topic() default "";
    /** group of consumption */
    String group() default "";
}
