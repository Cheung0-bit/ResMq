package io.github.resmq.dashboard;

import io.github.resmq.core.constant.Constants;
import io.github.resmq.dashboard.entity.*;
import io.github.resmq.dashboard.service.TopicsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

@SpringBootTest
@Slf4j
class ResmqDashboardApplicationTests {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    TopicsService topicsService;

    @Test
    void count() {
        Map<String, Integer> map = new HashMap<>();
        Integer topicCount = Optional.ofNullable(stringRedisTemplate.keys(Constants.GROUP_COUNT_KEY + "*"))
                .map(Set::size)
                .orElse(0);
        map.put("topicCount", topicCount);
        long now = System.currentTimeMillis() / (24 * 60 * 60 * 1000) - 1;
        String key = Constants.MESSAGE_COUNT_KEY + now + "*";
        Set<String> keys = stringRedisTemplate.keys(key);
        if (keys == null || keys.isEmpty()) {
            map.put("totalCount", 0);
            map.put("ackCount", 0);
            map.put("dlqCount", 0);
        } else {
            int totalCount = 0, ackCount = 0, dlqCount = 0;
            for (String s : keys) {
                Integer totalCountValue = Optional.ofNullable(stringRedisTemplate.opsForHash().get(s, "total-count"))
                        .map(value -> Integer.parseInt(value.toString()))
                        .orElse(0);
                totalCount += totalCountValue;

                Integer ackCountValue = Optional.ofNullable(stringRedisTemplate.opsForHash().get(s, "ack-count"))
                        .map(value -> Integer.parseInt(value.toString()))
                        .orElse(0);
                ackCount += ackCountValue;

                Integer dlqCountValue = Optional.ofNullable(stringRedisTemplate.opsForHash().get(s, "dlq-count"))
                        .map(value -> Integer.parseInt(value.toString()))
                        .orElse(0);
                dlqCount += dlqCountValue;
            }
            map.put("totalCount", totalCount);
            map.put("ackCount", ackCount);
            map.put("dlqCount", dlqCount);
        }
        System.out.println(1);
    }

    @Test
    void contextLoads() {
        Set<String> keys = stringRedisTemplate.keys(Constants.TOPIC_PREFIX + "*");
        List<CommonMessageSummary> commonMessageSummaryList = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                if (!key.contains("DLQ")) {
                    TopicInfo topicInfo = topicsService.getTopicInfo(key);
                    if (topicInfo != null && topicInfo.getGroups() > 0) {
                        CommonMessageSummary commonMessageSummary = new CommonMessageSummary();
                        String topic = key.substring(Constants.TOPIC_PREFIX.length());
                        commonMessageSummary.setOriginTopic(topic);
                        List<CommonGroupMessage> commonGroupMessageList = new ArrayList<>();
                        List<GroupInfo> topicDetail = topicsService.getTopicDetail(topic);
                        for (GroupInfo groupInfo : topicDetail) {
                            CommonGroupMessage commonGroupMessage = new CommonGroupMessage();
                            commonGroupMessage.setGroupName(groupInfo.getName());
                            int count = (int) groupInfo.getPending();
                            String lastDeliveredId = groupInfo.getLastDeliveredId();
                            List<MapRecord<String, Object, Object>> range = stringRedisTemplate.opsForStream().reverseRange(key, Range.closed("0-0", lastDeliveredId), Limit.limit().count(count));
                            List<CommonMessage> commonMessageList = range.stream().map(
                                    e -> new CommonMessage(e.getId().toString(), e.getValue().get("message").toString())
                            ).toList();
                            commonGroupMessage.setCommonMessages(commonMessageList);
                            commonGroupMessageList.add(commonGroupMessage);
                        }
                        commonMessageSummary.setCommonGroupMessages(commonGroupMessageList);
                        commonMessageSummaryList.add(commonMessageSummary);
                    }
                }
            }
        } else {
            log.info("topics为空");
        }
    }

}
