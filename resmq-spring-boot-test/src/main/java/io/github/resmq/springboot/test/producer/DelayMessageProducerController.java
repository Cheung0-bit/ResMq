package io.github.resmq.springboot.test.producer;

import io.github.resmq.core.template.ResMqTemplate;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/4/24 11:21
 */
@RestController
public class DelayMessageProducerController {

    @Resource
    private ResMqTemplate resMqTemplate;

    @GetMapping("/delay/sendMessage")
    public void sendMessage() {
        try {
            boolean res = resMqTemplate.syncDelaySend("delay-message-test", "test message",
                    30, TimeUnit.SECONDS);
            if (!res) {
                System.out.println("消息投递失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
