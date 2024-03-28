package io.github.resmq.springboot.test.util;

import io.github.resmq.core.constant.Constants;
import io.github.resmq.core.util.LuaScriptUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class LuaScriptUtilTest {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    static final String TOPIC = Constants.TOPIC_PREFIX +  "redis-topic";
    static final String GROUP = "default-group";

    @Test
    void getInfoGroups() {
        StreamInfo.XInfoGroups infoGroups = LuaScriptUtil.getInfoGroups(TOPIC, stringRedisTemplate);
        if (infoGroups != null) {
            for (StreamInfo.XInfoGroup xInfoGroup : infoGroups) {
                System.out.println("Group Name : " + xInfoGroup.groupName());
                System.out.println("Consumer Count : " + xInfoGroup.consumerCount());
                System.out.println("Pending Count : " + xInfoGroup.pendingCount());
                System.out.println("Last Delivered Id : " + xInfoGroup.lastDeliveredId());
                System.out.println("==========================");
            }
        }
    }

    @Test
    void createGroup() {
        boolean isSuccess = LuaScriptUtil.createGroup(TOPIC, ReadOffset.from("0-0"), GROUP, stringRedisTemplate);
        System.out.println(isSuccess);
    }

    @Test
    void getPendingMessagesSummary() {
        PendingMessagesSummary pendingMessagesSummary = LuaScriptUtil.getPendingMessagesSummary(TOPIC, GROUP, stringRedisTemplate);
        if (pendingMessagesSummary != null) {
            System.out.println("Total Pending Messages : " + pendingMessagesSummary.getTotalPendingMessages());
            System.out.println("Group Name : " + pendingMessagesSummary.getGroupName());
            System.out.println("Id Low : " + pendingMessagesSummary.getIdRange().getLowerBound()
                    + " Id Upper : " + pendingMessagesSummary.getIdRange().getUpperBound());
        }
    }

    @Test
    void getPendingMessages() {
        PendingMessages pendingMessages = LuaScriptUtil.getPendingMessages(TOPIC, GROUP, "consumer", Range.closed("-", "+"), 10, stringRedisTemplate);
        for (PendingMessage pendingMessage : pendingMessages) {
            System.out.println("Id : " + pendingMessage.getIdAsString());
            System.out.println("Consumer : " + pendingMessage.getConsumerName());
            System.out.println("Total Delivery Count : " + pendingMessage.getTotalDeliveryCount());
            System.out.println("=======================================");
        }
    }

    @Test
    void delayMessage() {
        String key = Constants.DELAY_MESSAGE_TTL_PREFIX_KEY + TOPIC;
        int delayTime = 3000;
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long timestamp = timeUnit.toSeconds(delayTime);
        long now = System.currentTimeMillis() / 1000;
        timestamp += now;
        Boolean isSuccess = stringRedisTemplate.opsForZSet().add(key, "message", timestamp);
//        if (isSuccess != null && isSuccess.equals(Boolean.TRUE)) {
//
//        }
    }

}