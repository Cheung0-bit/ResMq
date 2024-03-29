package io.github.resmq.starter.config;

import io.github.resmq.core.annotation.ResMqListenerAnnotationBeanPostProcessor;
import io.github.resmq.core.config.ResMqProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * pre auto configuration
 *
 * @author zhanglin
 */
@Configuration
@ConditionalOnProperty(name = "res-mq.enable", havingValue = "true")
public class ResMqPreAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "res-mq")
    ResMqProperties resMqProperties() {
        // add banner
        System.out.println("\n" +
                "   ___          __  ___    \n" +
                "  / _ \\___ ___ /  |/  /__ _\n" +
                " / , _/ -_|_-</ /|_/ / _ `/\n" +
                "/_/|_|\\__/___/_/  /_/\\_, / \n" +
                "                      /_/   Repo: https://github.com/Cheung0-bit/ResMq\n");
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
