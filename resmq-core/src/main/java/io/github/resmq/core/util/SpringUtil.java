package io.github.resmq.core.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <Spring工具>
 *
 * @author zhanglin
 */
@Component
public class SpringUtil implements BeanFactoryPostProcessor, ApplicationContextAware {

    /**
     * The "@PostConstruct" annotation marks a class with a null pointer because the ApplicationContext has not yet been loaded<br>
     * So spring BeanFactoryPostProcessor pumped ConfigurableListableBeanFactory implementation bean operation
     */
    private static ConfigurableListableBeanFactory beanFactory;
    /**
     * The Spring application context
     */
    private static ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtil.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * 获取{@link ApplicationContext}
     *
     * @return {@link ApplicationContext}
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取{@link ListableBeanFactory}，可能为{@link ConfigurableListableBeanFactory} 或 {@link ApplicationContextAware}
     *
     * @return {@link ListableBeanFactory}
     */
    public static ListableBeanFactory getBeanFactory() {
        return null == beanFactory ? applicationContext : beanFactory;
    }

    /**
     * 获取{@link ConfigurableListableBeanFactory}
     *
     * @return {@link ConfigurableListableBeanFactory}
     */
    public static ConfigurableListableBeanFactory getConfigurableBeanFactory() {
        final ConfigurableListableBeanFactory factory;
        if (null != beanFactory) {
            factory = beanFactory;
        } else if (applicationContext instanceof ConfigurableApplicationContext) {
            factory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
        } else {
            throw new BeanDefinitionStoreException("No ConfigurableListableBeanFactory from context!");
        }
        return factory;
    }

    //通过name获取 Bean.

    /**
     * Get the Bean by name
     *
     * @param <T>  Bean Type
     * @param name Bean Name
     * @return Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) getBeanFactory().getBean(name);
    }

    /**
     * obtain bean by class
     *
     * @param <T>   Bean Type
     * @param clazz Bean Class
     * @return Bean对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return getBeanFactory().getBean(clazz);
    }

    /**
     * Returns the specified Bean by name and Clazz
     *
     * @param <T>   bean type
     * @param name  Bean name
     * @param clazz bean class
     * @return Bean对象
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getBeanFactory().getBean(name, clazz);
    }

    /**
     * Retrieves all beans of the specified type, including subclasses
     *
     * @param <T>  Bean Type
     * @param type Class, interface, null means get all beans
     * @return The key is the registered name of the Bean, and the value is the bean
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return getBeanFactory().getBeansOfType(type);
    }

    /**
     * Gets the Bean name for the specified type, including subclasses
     *
     * @param type Class, interface, null to get all bean names
     * @return bean name
     */
    public static String[] getBeanNamesForType(Class<?> type) {
        return getBeanFactory().getBeanNamesForType(type);
    }

    /**
     * Gets the value of the configuration item of the configuration file
     *
     * @param key Configuration key
     * @return Attribute values
     */
    public static String getProperty(String key) {
        if (null == applicationContext) {
            return null;
        }
        return applicationContext.getEnvironment().getProperty(key);
    }

    /**
     * Gets the current environment config; null is returned if no configuration is available
     *
     * @return The current environment configuration
     */
    public static String[] getActiveProfiles() {
        if (null == applicationContext) {
            return new String[]{};
        }
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    /**
     * Get the current environment configuration, and when there are multiple environment configurations, only get the first one
     *
     * @return The current environment configuration
     */
    public static String getActiveProfile() {
        final String[] activeProfiles = getActiveProfiles();
        return (activeProfiles.length != 0) ? activeProfiles[0] : null;
    }

    /**
     * Beans are dynamically registered with Spring
     * <p>
     * {@link org.springframework.beans.factory.BeanFactory}
     * <p>
     *
     * @param <T>      Bean类型
     * @param beanName 名称
     * @param bean     bean
     */
    public static <T> void registerBean(String beanName, T bean) {
        final ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        factory.autowireBean(bean);
        factory.registerSingleton(beanName, bean);
    }

    /**
     * Logging out beans
     * Use caution when logging out Spring beans
     *
     * @param beanName bean name
     */
    public static void unregisterBean(String beanName) {
        final ConfigurableListableBeanFactory factory = getConfigurableBeanFactory();
        if (factory instanceof DefaultSingletonBeanRegistry) {
            DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) factory;
            registry.destroySingleton(beanName);
        } else {
            throw new BeanDefinitionStoreException("Can not unregister bean, the factory is not a DefaultSingletonBeanRegistry!");
        }
    }
}
