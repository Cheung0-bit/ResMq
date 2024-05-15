package io.github.resmq.springboot.test.consumer;

import io.github.resmq.core.annotation.ResMqListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/4/24 11:23
 */
@Component
public class DelayMessageConsume {

    @ResMqListener(
            topic = "delay-message-test",
            group = "default-group"
    )
    public void receive(String message) throws InterruptedException {
        // 模拟消费者执行业务时间
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("Topic: delay-message-test, Group: default-group, Message: " + message);
    }

}
