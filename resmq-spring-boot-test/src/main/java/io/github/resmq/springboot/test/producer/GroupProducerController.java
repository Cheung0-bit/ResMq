package io.github.resmq.springboot.test.producer;

import io.github.resmq.core.template.ResMqTemplate;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/4/24 11:11
 */
@RestController
public class GroupProducerController {

    @Resource
    private ResMqTemplate resMqTemplate;

    @GetMapping("/group/sendMessage")
    public void sendMessage() {
        try {
            boolean res = resMqTemplate.syncSend("group-test", "test message");
            if (!res) {
                System.out.println("消息投递失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
