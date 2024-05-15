package io.github.resmq.springboot.test.consumer;

import io.github.resmq.core.annotation.ResMqListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/4/24 11:08
 */
@Component
public class GroupConsume {

    @ResMqListener(
            topic = "group-test",
            group = "group1"
    )
    public void receive11(String message) throws InterruptedException {
        // 模拟消费者执行业务时间
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("Topic: group-test, Group: group1, Message: " + message);
    }

    @ResMqListener(
            topic = "group-test",
            group = "group2"
    )
    public void receive12(String message) throws InterruptedException {
        // 模拟消费者执行业务时间
        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("Topic: group-test, Group: group2, Message: " + message);
    }

}
