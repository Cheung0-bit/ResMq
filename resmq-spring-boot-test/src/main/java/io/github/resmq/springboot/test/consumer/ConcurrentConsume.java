package io.github.resmq.springboot.test.consumer;

import io.github.resmq.core.annotation.ResMqListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/4/24 11:18
 */
@Component
public class ConcurrentConsume {

    @ResMqListener(
            topic = "concurrent-consume-test",
            group = "default-group"
    )
    public void receive1(String message) throws InterruptedException {
        // 模拟消费者执行业务时间
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("Consumer1 Topic: concurrent-consume-test, Group: default-group, Message: " + message);
    }

    @ResMqListener(
            topic = "concurrent-consume-test",
            group = "default-group"
    )
    public void receive2(String message) throws InterruptedException {
        // 模拟消费者执行业务时间
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("Consumer2 Topic: concurrent-consume-test, Group: default-group, Message: " + message);
    }

    @ResMqListener(
            topic = "concurrent-consume-test",
            group = "default-group"
    )
    public void receive3(String message) throws InterruptedException {
        // 模拟消费者执行业务时间
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("Consumer3 Topic: concurrent-consume-test, Group: default-group, Message: " + message);
    }
}
