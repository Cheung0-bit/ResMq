package io.github.resmq.dashboard.service.impl;

import io.github.resmq.core.constant.Constants;
import io.github.resmq.dashboard.entity.DeadGroupMessage;
import io.github.resmq.dashboard.entity.DeadMessage;
import io.github.resmq.dashboard.entity.DeadMessageSummary;
import io.github.resmq.dashboard.service.DeadMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/2/2 18:42
 */
@Service
@Slf4j
public class DeadMessageImpl implements DeadMessageService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<DeadMessageSummary> getDMSummary() {
        // 获取topics的key
        Set<String> keys = stringRedisTemplate.keys(Constants.TOPIC_PREFIX + "*");
        List<DeadMessageSummary> deadMessageSummaryList = null;
        if (keys != null) {
            Map<String, DeadMessageSummary> map = new HashMap<>();
            for (String key : keys) {
                if (key.contains("DLQ")) {
                    String[] res = key.substring(Constants.TOPIC_PREFIX.length()).split(":");
                    String originTopic = res[0];
                    if (!map.containsKey(originTopic)) {
                        DeadMessageSummary deadMessageSummary = new DeadMessageSummary();
                        deadMessageSummary.setOriginTopic(originTopic);
                        List<DeadGroupMessage> deadGroupMessages = new ArrayList<>();
                        convertToDeadGroupMessage(key, res, deadGroupMessages);
                        deadMessageSummary.setDeadGroupMessages(deadGroupMessages);
                        map.put(originTopic, deadMessageSummary);
                    } else {
                        List<DeadGroupMessage> deadGroupMessages = map.get(originTopic).getDeadGroupMessages();
                        convertToDeadGroupMessage(key, res, deadGroupMessages);
                    }
                }
            }
            deadMessageSummaryList = new ArrayList<>(map.values());
        } else {
            log.info("topics为空");
        }
        return deadMessageSummaryList;
    }

    private void convertToDeadGroupMessage(String key, String[] res, List<DeadGroupMessage> deadGroupMessages) {
        DeadGroupMessage deadGroupMessage = new DeadGroupMessage();
        deadGroupMessage.setGroupName(res[2]);
        List<MapRecord<String, Object, Object>> messages = stringRedisTemplate.opsForStream().range(key, Range.rightOpen("0-0", "+"));
        if (messages != null) {
            List<DeadMessage> deadMessages = messages.stream().map(
                    e -> new DeadMessage(e.getId().toString(), e.getValue().get("message").toString())
            ).toList();
            deadGroupMessage.setDeadMessages(deadMessages);
        }
        deadGroupMessages.add(deadGroupMessage);
    }
}
