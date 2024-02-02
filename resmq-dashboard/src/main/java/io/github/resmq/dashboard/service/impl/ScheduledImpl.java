package io.github.resmq.dashboard.service.impl;

import io.github.resmq.core.constant.Constants;
import io.github.resmq.dashboard.entity.ScheduledInfo;
import io.github.resmq.dashboard.entity.ScheduledTask;
import io.github.resmq.dashboard.service.ScheduledService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/2/2 14:55
 */
@Service
public class ScheduledImpl implements ScheduledService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<ScheduledInfo> getScheduledMessages() {
        Set<String> keys = stringRedisTemplate.keys(Constants.DELAY_MESSAGE_TTL_PREFIX_KEY + "*");
        List<ScheduledInfo> scheduledInfos = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                ZSetOperations<String, String> zSetOperations = stringRedisTemplate.opsForZSet();
                Long member = zSetOperations.zCard(key);
                ScheduledInfo info = new ScheduledInfo();
                info.setMessages(member - 1);
                info.setScheduledName(key.substring(Constants.DELAY_MESSAGE_TTL_PREFIX_KEY.length()));
                scheduledInfos.add(info);
            }
        }
        return scheduledInfos;
    }

    @Override
    public List<ScheduledTask> getScheduledTasks(String key) {
        Set<ZSetOperations.TypedTuple<String>> res = stringRedisTemplate.opsForZSet().rangeByScoreWithScores(Constants.DELAY_MESSAGE_TTL_PREFIX_KEY + key, 1, Double.MAX_VALUE);
        List<ScheduledTask> tasks = new ArrayList<>();
        if (res != null) {
            for (ZSetOperations.TypedTuple<String> r : res) {
                ScheduledTask scheduledTask = new ScheduledTask();
                scheduledTask.setMessage(r.getValue());
                Double score = r.getScore();
                Assert.isTrue(score != null, "system error");
                double now = System.currentTimeMillis() / 1000.0;
                scheduledTask.setExpired(score < now);
                LocalDateTime deadline = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(score.longValue())
                        , ZoneId.systemDefault()
                );
                scheduledTask.setDeadline(deadline);
                tasks.add(scheduledTask);
            }
        }
        return tasks;
    }
}
