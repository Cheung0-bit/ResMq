package io.github.resmq.dashboard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.core.constant.Constants;
import io.github.resmq.core.util.SpringUtil;
import io.github.resmq.dashboard.controller.BaseController;
import io.github.resmq.dashboard.entity.TopicInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SpringBootTest
@Slf4j
class ResmqDashboardApplicationTests {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
        // 获取topics的key
        Set<String> keys = stringRedisTemplate.keys(Constants.TOPIC_PREFIX + "*");
        List<TopicInfo> topicInfos = new ArrayList<>();
        if (keys != null) {
            log.info(Arrays.toString(keys.toArray()));
            for (String key : keys) {
                StreamInfo.XInfoStream info = stringRedisTemplate.opsForStream().info(key);
                TopicInfo topicInfo = JSON.parseObject(JSON.toJSONString(info.getRaw()), TopicInfo.class);
                topicInfos.add(topicInfo);
            }
        } else {
            log.info("topics为空");
        }
    }

}
