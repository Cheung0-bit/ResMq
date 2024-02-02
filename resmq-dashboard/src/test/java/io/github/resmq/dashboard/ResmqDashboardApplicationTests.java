package io.github.resmq.dashboard;

import io.github.resmq.core.constant.Constants;
import io.github.resmq.core.util.LuaScriptUtil;
import io.github.resmq.dashboard.entity.GroupInfo;
import io.github.resmq.dashboard.entity.TopicInfo;
import io.github.resmq.dashboard.service.TopicsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.PendingMessagesSummary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@SpringBootTest
@Slf4j
class ResmqDashboardApplicationTests {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    TopicsService topicsService;

    @Test
    void contextLoads() {
        Set<String> keys = stringRedisTemplate.keys(Constants.TOPIC_PREFIX + "*");
        if (keys != null) {
            for (String key : keys) {
                if (!key.contains("DLQ")) {
                    TopicInfo topicInfo = topicsService.getTopicInfo(key);
                    if (topicInfo != null && topicInfo.getGroups() > 0) {
                        // todo
                        List<GroupInfo> topicDetail = topicsService.getTopicDetail(key.substring(Constants.TOPIC_PREFIX.length()));
                        System.out.println(1);
                    }
                }
            }
        } else {
            log.info("topics为空");
        }
        System.out.println(1);
    }

}
