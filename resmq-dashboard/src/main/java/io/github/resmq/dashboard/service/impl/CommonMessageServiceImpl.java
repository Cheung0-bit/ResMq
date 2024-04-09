package io.github.resmq.dashboard.service.impl;

import io.github.resmq.core.constant.Constants;
import io.github.resmq.dashboard.entity.CommonMessage;
import io.github.resmq.dashboard.service.CommonMessageService;
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
 * @Author zhanglin
 * @createTime 2024/4/8 13:08
 */
@Service
@Slf4j
public class CommonMessageServiceImpl implements CommonMessageService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Map<String, Object> getCommonMessages(int start, int length, String topic) {
        Set<String> keys = new HashSet<>();
        if (topic.equals("-1")) {
            keys = stringRedisTemplate.keys(Constants.TOPIC_PREFIX + "*");
        } else if (!topic.equals("0")) {
            keys.add(Constants.TOPIC_PREFIX + topic);
        }
        List<CommonMessage> commonMessages = new ArrayList<>();
        if (keys != null) {
            for (String key : keys) {
                if (!key.contains("DLQ")) {
                    List<MapRecord<String, Object, Object>> messages = stringRedisTemplate.opsForStream().range(key, Range.rightOpen("0-0", "+"));
                    if (messages != null) {
                        commonMessages.addAll(messages.stream().map(
                                e -> new CommonMessage(e.getId().toString(), e.getValue().get("message").toString())
                        ).toList());
                    }
                }
            }
        } else {
            log.info("topics为空");
        }
        List<CommonMessage> paginated = PaginationUtils.paginate(commonMessages, start, length);
        Map<String, Object> map = new HashMap<>();
        map.put("recordsTotal", commonMessages.size());             // 总记录数
        map.put("recordsFiltered", commonMessages.size());           // 过滤后的总记录数
        map.put("data", paginated);                             // 分页列表
        return map;
    }
}
