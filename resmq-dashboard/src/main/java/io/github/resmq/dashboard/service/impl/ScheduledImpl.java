package io.github.resmq.dashboard.service.impl;

import io.github.resmq.core.constant.Constants;
import io.github.resmq.dashboard.entity.ScheduledInfo;
import io.github.resmq.dashboard.entity.ScheduledTask;
import io.github.resmq.dashboard.entity.TopicInfo;
import io.github.resmq.dashboard.service.ScheduledService;
import io.github.resmq.dashboard.util.PaginationUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/2/2 14:55
 */
@Service
public class ScheduledImpl implements ScheduledService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<ScheduledTask> getScheduledTasks(String scheduledName) {
        Set<String> keys = stringRedisTemplate.keys(Constants.DELAY_MESSAGE_TTL_PREFIX_KEY + "*");
        List<ScheduledTask> scheduledTasks = new ArrayList<>();
        Pattern pattern = Pattern.compile(scheduledName);
        if (keys != null) {
            for (String key : keys) {
                if (pattern.matcher(key.substring(Constants.DELAY_MESSAGE_TTL_PREFIX_KEY.length())).find()) {
                    scheduledTasks.addAll(getByKey(key));
                }
            }
        }
        return scheduledTasks;
    }

    @Override
    public Map<String, Object> returnScheduledTasks(int start, int length, String scheduledName) {
        List<ScheduledTask> scheduledTasks = getScheduledTasks(scheduledName);
        List<ScheduledTask> paginated = PaginationUtils.paginate(scheduledTasks, start, length);
        Map<String, Object> map = new HashMap<>();
        map.put("recordsTotal", scheduledTasks.size());             // 总记录数
        map.put("recordsFiltered", scheduledTasks.size());           // 过滤后的总记录数
        map.put("data", paginated);                             // 分页列表
        return map;
    }

    private List<ScheduledTask> getByKey(String key) {
        Set<ZSetOperations.TypedTuple<String>> res = stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, 1, Double.MAX_VALUE);
        List<ScheduledTask> tasks = new ArrayList<>();
        if (res != null) {
            for (ZSetOperations.TypedTuple<String> r : res) {
                ScheduledTask scheduledTask = new ScheduledTask();
                scheduledTask.setScheduledName(key.substring(Constants.DELAY_MESSAGE_TTL_PREFIX_KEY.length()));
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
