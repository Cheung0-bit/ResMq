package io.github.resmq.dashboard.service.impl;

import io.github.resmq.core.constant.Constants;
import io.github.resmq.dashboard.entity.CommonGroupMessage;
import io.github.resmq.dashboard.entity.CommonMessage;
import io.github.resmq.dashboard.entity.CommonMessageSummary;
import io.github.resmq.dashboard.service.DeadMessageService;
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
    public List<CommonMessageSummary> getDMSummary() {
        // 获取topics的key
        Set<String> keys = stringRedisTemplate.keys(Constants.TOPIC_PREFIX + "*");
        List<CommonMessageSummary> commonMessageSummaryList = null;
        if (keys != null) {
            Map<String, CommonMessageSummary> map = new HashMap<>();
            for (String key : keys) {
                if (key.contains("DLQ")) {
                    String[] res = key.substring(Constants.TOPIC_PREFIX.length()).split(":");
                    String originTopic = res[0];
                    if (!map.containsKey(originTopic)) {
                        CommonMessageSummary commonMessageSummary = new CommonMessageSummary();
                        commonMessageSummary.setOriginTopic(originTopic);
                        List<CommonGroupMessage> commonGroupMessages = new ArrayList<>();
                        convertToDeadGroupMessage(key, res, commonGroupMessages);
                        commonMessageSummary.setCommonGroupMessages(commonGroupMessages);
                        map.put(originTopic, commonMessageSummary);
                    } else {
                        List<CommonGroupMessage> commonGroupMessages = map.get(originTopic).getCommonGroupMessages();
                        convertToDeadGroupMessage(key, res, commonGroupMessages);
                    }
                }
            }
            commonMessageSummaryList = new ArrayList<>(map.values());
        } else {
            log.info("topics为空");
        }
        return commonMessageSummaryList;
    }

    private void convertToDeadGroupMessage(String key, String[] res, List<CommonGroupMessage> commonGroupMessages) {
        CommonGroupMessage commonGroupMessage = new CommonGroupMessage();
        commonGroupMessage.setGroupName(res[2]);
        List<MapRecord<String, Object, Object>> messages = stringRedisTemplate.opsForStream().range(key, Range.rightOpen("0-0", "+"));
        if (messages != null) {
            List<CommonMessage> commonMessages = messages.stream().map(
                    e -> new CommonMessage(e.getId().toString(), e.getValue().get("message").toString())
            ).toList();
            commonGroupMessage.setCommonMessages(commonMessages);
        }
        commonGroupMessages.add(commonGroupMessage);
    }
}
