package io.github.resmq.dashboard.service.impl;

import io.github.resmq.core.constant.Constants;
import io.github.resmq.dashboard.entity.DeadMessage;
import io.github.resmq.dashboard.service.DeadMessageService;
import io.github.resmq.dashboard.util.PaginationUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/2/2 18:42
 */
@Service
@Slf4j
public class DeadMessageImpl implements DeadMessageService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Map<String, Object> getDeadMessages(int start, int length, String topic) {
        Set<String> keys = new HashSet<>();
        if (!topic.equals("0")) {
            keys = stringRedisTemplate.keys(Constants.TOPIC_PREFIX + "*");
            if (keys != null) {
                keys.removeIf(key -> !key.contains("DLQ"));
                if (!topic.equals("-1")) {
                    keys.removeIf(key -> !key.contains(topic));
                }
            }
        }
        List<DeadMessage> deadMessages = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                String[] split = key.substring(Constants.TOPIC_PREFIX.length()).split(":");
                String originTopic = split[0];
                String groupName = split[2];
                List<MapRecord<String, Object, Object>> messages = stringRedisTemplate.opsForStream().range(key, Range.rightOpen("0-0", "+"));
                if (messages != null) {
                    deadMessages.addAll(messages.stream().map(
                            e -> new DeadMessage(e.getId().toString(), e.getValue().get("message").toString(), originTopic, groupName)
                    ).toList());
                }
            }
        } else {
            log.info("topics为空");
        }
        List<DeadMessage> paginated = PaginationUtils.paginate(deadMessages, start, length);
        Map<String, Object> map = new HashMap<>();
        map.put("recordsTotal", deadMessages.size());             // 总记录数
        map.put("recordsFiltered", deadMessages.size());           // 过滤后的总记录数
        map.put("data", paginated);                             // 分页列表
        return map;
    }

}
