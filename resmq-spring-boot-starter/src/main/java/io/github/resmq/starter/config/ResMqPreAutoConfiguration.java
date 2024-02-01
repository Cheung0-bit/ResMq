package io.github.resmq.starter.config;

import com.alibaba.fastjson.JSON;
import io.github.resmq.core.annotation.ResMqListenerAnnotationBeanPostProcessor;
import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.core.constant.Constants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * <预先配置>
 *
 * @Author zhanglin
 * @createTime 2024/1/30 16:25
 */
@Configuration
@ConditionalOnProperty(name = "res-mq.enable", havingValue = "true")
public class ResMqPreAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "res-mq")
    ResMqProperties resMqProperties() {
        return new ResMqProperties();
    }

    /**
     * static装饰: 先初始化，是BeanPostProcessor拦截器
     *
     * @return
     */
    @Bean
    static ResMqListenerAnnotationBeanPostProcessor resMqListenerAnnotationBeanPostProcessor() {
        return new ResMqListenerAnnotationBeanPostProcessor();
    }

}
