package io.github.resmq.springboot.test.controller;

import io.github.resmq.core.annotation.ResMqListener;
import io.github.resmq.core.template.ResMqTemplate;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * <测试>
 *
 * @author zhanglin
 * @createTime 2024/1/30 21:32
 */
@RestController
@Slf4j
public class TestController {

    @Resource
    ResMqTemplate resMqTemplate;

    /**
     * 业务名称：对应配置文件 res-mq.streams.xxx
     */
    public static final String BUSINESS_NAME = "email-send";
    /**
     * 主题名称：对应配置文件 res-mq.streams.email-send.topic
     */
    public static final String TOPIC = "redis-topic";
    /**
     * 分组名称：对应配置文件 res-mq.streams.email-send.group
     */
    public static final String GROUP = "another-group";

    @GetMapping("/mq/redis/sendMessage")
    public void sendRedisMessage() {
        Email email = new Email("test email", "nothing", "bruce");
        try {
            resMqTemplate.syncSend(TOPIC, email);
            resMqTemplate.syncSend("sys-log", email);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ResMqListener(name = BUSINESS_NAME)
    public void receiveByName(Email email) {
        log.info("[{}]Receive Message--->{}", "through business name", email);
        throw new RuntimeException("unAck error");
    }
    @ResMqListener(topic = TOPIC, group = "group1")
    public void receiveByTopic1Group1(Email email) {
        log.info("[{}]Receive Message--->{}", "through topic1 group1", email);
        throw new RuntimeException("unAck error");
    }

    @ResMqListener(topic = TOPIC, group = "group2")
    public void receiveByTopic1Group2(Email email) {
        log.info("[{}]Receive Message--->{}", "through topic1 group2", email);
        throw new RuntimeException("unAck error");
    }

    @ResMqListener(topic = "sys-log", group = "group1")
    public void receiveByTopic2Group1(Email email) {
        log.info("[{}]Receive Message--->{}", "through topic2 group1", email);
        throw new RuntimeException("unAck error");
    }

//    @ResMqListener(
//            topic = "redis-topic:DLQ:default-group"
//            , group = "default-group"
//    )
//    public void receiveDeadMessage(Email email) {
//        log.error("Receive Dead Message111--->{}", email);
//    }

    @GetMapping("/mq/redis/sendDelayMessage")
    public void sendRedisDelayMessage() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                Email email = new Email("test email " + i + j, "something", "bruce");
                try {
                    resMqTemplate.syncDelaySend("order-service", email, 10, TimeUnit.SECONDS);
                    resMqTemplate.syncDelaySend("video-transcode", email, 10, TimeUnit.HOURS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println(i);
            TimeUnit.SECONDS.sleep(2);
        }
    }

    @ResMqListener(name = "order-service")
    public void receiveDelayMessage1(Email email) {
        log.info("Receive Delay Message111--->{}", email.toString());
//        throw new RuntimeException("error 111");
    }

    @ResMqListener(
            topic = "order-service",
            group = GROUP
    )
    public void receiveDelayMessage2(Email email) {
        log.info("Receive Delay Message222--->{}", email.toString());
//        throw new RuntimeException("error 111");
    }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
class Email {
    String title;
    String text;
    String author;
}
