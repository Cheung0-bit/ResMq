package io.github.resmq.dashboard.service.impl;

import com.alibaba.fastjson.JSON;
import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.core.constant.Constants;
import io.github.resmq.dashboard.service.IndexParamService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/1/31 20:41
 */
@Service
public class IndexParamServiceImpl implements IndexParamService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void getClusterNodes() {

    }

    @Override
    public ResMqProperties getResMqProperties() {
        String jsonStr = stringRedisTemplate.opsForValue().get(Constants.CONFIGURATION_KEY);
        return JSON.parseObject(jsonStr, ResMqProperties.class);
    }

    @Override
    public Map<String, Integer> getCount() {
        Map<String, Integer> map = new HashMap<>();
        Integer topicCount = Optional.ofNullable(stringRedisTemplate.keys(Constants.GROUP_COUNT_KEY + "*"))
                .map(Set::size)
                .orElse(0);
        map.put("topicCount", topicCount);
        long now = System.currentTimeMillis() / (24 * 60 * 60 * 1000);
        String key = Constants.MESSAGE_COUNT_KEY + now + "*";
        Set<String> countKeys = stringRedisTemplate.keys(key);
        if (countKeys == null || countKeys.isEmpty()) {
            map.put("totalCount", 0);
            map.put("dlqCount", 0);
        } else {
            int totalCount = 0, ackCount = 0, dlqCount = 0;
            for (String countKey : countKeys) {
                Integer totalCountValue = Optional.ofNullable(stringRedisTemplate.opsForHash().get(countKey, "total-count"))
                        .map(value -> Integer.parseInt(value.toString()))
                        .orElse(0);
                totalCount += totalCountValue;

                Integer dlqCountValue = Optional.ofNullable(stringRedisTemplate.opsForHash().get(countKey, "dlq-count"))
                        .map(value -> Integer.parseInt(value.toString()))
                        .orElse(0);
                dlqCount += dlqCountValue;
            }
            map.put("totalCount", totalCount);
            map.put("dlqCount", dlqCount);
        }
        Set<String> delayKeys = stringRedisTemplate.keys(Constants.DELAY_MESSAGE_TTL_PREFIX_KEY + "*");
        if (delayKeys == null || delayKeys.isEmpty()) {
            map.put("delayCount", 0);
        } else {
            int delayCount = 0;
            for (String delayKey : delayKeys) {
                delayCount += Optional.ofNullable(stringRedisTemplate.opsForZSet().zCard(delayKey))
                        .map(Long::intValue)
                        .orElse(0) - 1;
            }
            map.put("delayCount", delayCount);
        }
        return map;
    }
}
