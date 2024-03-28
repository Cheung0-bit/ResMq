package io.github.resmq.starter.config;

import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.core.constant.Constants;
import io.github.resmq.core.exception.ResMqException;
import io.github.resmq.core.listener.ResMqListenerBean;
import io.github.resmq.core.listener.ResMqListenerParam;
import io.github.resmq.core.task.ResMqDeadMessageTasker;
import io.github.resmq.core.task.ResMqDelayMessageTasker;
import io.github.resmq.core.util.ConsumerUtil;
import io.github.resmq.core.util.LuaScriptUtil;
import io.github.resmq.core.annotation.ResMqListenerAnnotationBeanPostProcessor;
import io.github.resmq.core.template.ResMqTemplate;
import io.github.resmq.core.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * post auto configuration
 *
 * @author zhanglin
 */
@Configuration
@AutoConfigureAfter(ResMqPreAutoConfiguration.class)
@ConditionalOnBean(ResMqProperties.class)
public class ResMqPostAutoConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    @ConditionalOnMissingBean
    ResMqTemplate resMqTemplate(StringRedisTemplate stringRedisTemplate) {
        return new ResMqTemplate(stringRedisTemplate);
    }

    /**
     * 创建 Redis Stream 集群消费的容器
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory,
            ResMqListenerAnnotationBeanPostProcessor rmq, StringRedisTemplate stringRedisTemplate
    ) {
        rmq.handleListenerParameters(stringRedisTemplate);
        List<ResMqListenerParam> resMqListenerParams = rmq.getResMqListenerParamList();
        if (null == resMqListenerParams || resMqListenerParams.isEmpty()) {
            return null;
        }
        // 创建配置对象
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> containerOptions = null;
        // 根据配置对象创建监听容器对象
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container = null;
        containerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                // 一次性最多拉取多少条消息
                .batchSize(10)
                .errorHandler(Throwable::printStackTrace)
                // 超时时间，设置为0，表示不超时（超时后会抛出异常）
                .pollTimeout(Duration.ZERO)
                .serializer(new StringRedisSerializer())
                .build();
        // 根据配置对象创建监听容器对象
        container = StreamMessageListenerContainer.create(redisConnectionFactory, containerOptions);

        for (ResMqListenerParam resMqListenerParam : resMqListenerParams) {
            // 1、解析@ResMqListener
            String topic = resMqListenerParam.getTopic();
            String group = resMqListenerParam.getGroup();
            buildListener(stringRedisTemplate, container, resMqListenerParam, topic, group);
            // todo 原子性
            stringRedisTemplate.opsForSet().add(Constants.GROUP_COUNT_KEY + topic.substring(Constants.TOPIC_PREFIX.length()), group);
        }
        return container;
    }

    private void buildListener(StringRedisTemplate stringRedisTemplate, StreamMessageListenerContainer<String, MapRecord<String, String, String>> container, ResMqListenerParam resMqListenerParam, String topic, String group) {
        // 创建 listener 对应的消费者分组
        boolean createGroup = true;
        // Lua获取消费者组
        try {
            StreamInfo.XInfoGroups xgs = LuaScriptUtil.getInfoGroups(topic, stringRedisTemplate);
            if (xgs != null) {
                for (StreamInfo.XInfoGroup xg : xgs) {
                    if (xg.groupName().equals(group)) {
                        createGroup = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("createGroup error", e);
        }
        if (createGroup) {
            try {
                // 组别不存在 则新建对应组
                LuaScriptUtil.createGroup(topic, ReadOffset.from("0"), group, stringRedisTemplate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 创建 Consumer 对象
        Consumer consumer = null;
        try {
            consumer = Consumer.from(group, buildConsumerName());
        } catch (UnknownHostException e) {
            throw new ResMqException("创建消费者时，获取本地Address失败");
        }
        // 设置 Consumer 消费进度，以最小消费进度为准
        StreamOffset<String> streamOffset = StreamOffset.create(topic, ReadOffset.lastConsumed());
        // 设置 Consumer 监听
        StreamMessageListenerContainer.StreamReadRequestBuilder<String> builder = StreamMessageListenerContainer.StreamReadRequest
                .builder(streamOffset).consumer(consumer)
                // 不自动 ack
                .autoAcknowledge(false)
                // 默认配置，发生异常就取消消费，显然不符合预期；因此，我们设置为 false
                .cancelOnError(throwable -> false);
        container.register(builder.build(), new ResMqListenerBean(resMqListenerParam.getMethod(), resMqListenerParam.getBean()
                , stringRedisTemplate, topic, group));
        logger.info("Init ResMqListener, bean={}, method={}, topic={}, group={}", resMqListenerParam.getMethod().getName()
                , resMqListenerParam.getBean().getClass()
                , topic.substring(Constants.TOPIC_PREFIX.length()), group);
    }

    /**
     * 配置死信队列定时任务
     *
     * @param rmq
     * @param stringRedisTemplate
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    ResMqDeadMessageTasker resMqDeadMessageTasker(ResMqListenerAnnotationBeanPostProcessor rmq, StringRedisTemplate stringRedisTemplate) {
        List<ResMqListenerParam> resMqListenerParamList = rmq.getResMqListenerParamList();
        if (null == resMqListenerParamList || resMqListenerParamList.isEmpty()) {
            return null;
        }
        Set<String> topics = resMqListenerParamList.stream()
                .map(ResMqListenerParam::getTopic)
                .collect(Collectors.toSet());
        ResMqProperties resMqProperties = SpringUtil.getBean(ResMqProperties.class);
        return new ResMqDeadMessageTasker(stringRedisTemplate, topics, resMqProperties.getDeadMessageDeliveryCount(), resMqProperties.getDeadMessageDeliverySecond());
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    ResMqDelayMessageTasker resMqDelayMessageTasker(StringRedisTemplate stringRedisTemplate, ResMqTemplate resMqTemplate) {
        return new ResMqDelayMessageTasker(stringRedisTemplate, resMqTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(value = RedisMessageListenerContainer.class)
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    private String buildConsumerName() throws UnknownHostException {
        return String.format("%s@%d", ConsumerUtil.getHostAddress(), ConsumerUtil.getCurrentPid());
    }

}
