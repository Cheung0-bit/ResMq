package io.github.resmq.springboot.test.controller;

import io.github.resmq.core.template.ResMqTemplate;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/3/24 13:13
 */
@RestController
public class JmeterController {

    @Resource
    private ResMqTemplate resMqTemplate;

    @GetMapping("/jmeter/sendMessage")
    public void sendRedisMessage() {
        try {
            boolean res = resMqTemplate.syncSend("jmeter", "test message");
            if (!res) {
                System.out.println("消息投递失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
