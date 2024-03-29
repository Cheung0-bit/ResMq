package io.github.resmq.dashboard.service.impl;

import com.alibaba.fastjson.JSON;
import io.github.resmq.core.constant.Constants;
import io.github.resmq.core.util.LuaScriptUtil;
import io.github.resmq.dashboard.entity.GroupInfo;
import io.github.resmq.dashboard.entity.TopicInfo;
import io.github.resmq.dashboard.service.TopicsService;
import io.github.resmq.dashboard.util.PaginationUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/2/1 19:26
 */
@Service
@Slf4j
public class TopicsServiceImpl implements TopicsService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public TopicInfo getTopicInfo(String topic) {
        StreamInfo.XInfoStream info = stringRedisTemplate.opsForStream().info(topic);
        TopicInfo topicInfo = JSON.parseObject(JSON.toJSONString(info.getRaw()), TopicInfo.class);
        topicInfo.setName(topic.substring(Constants.TOPIC_PREFIX.length()));
        return topicInfo;
    }

    @Override
    public Map<String, Object> getAllTopicsInfo(int start, int length, String topic) {
        // 获取topics的key
        Set<String> keys = stringRedisTemplate.keys(Constants.TOPIC_PREFIX + "*");
        List<TopicInfo> topicInfos = new ArrayList<>();
        Pattern pattern = Pattern.compile(topic);
        if (keys != null) {
            for (String key : keys) {
                if (!key.contains("DLQ") &&
                        pattern.matcher(key.substring(Constants.TOPIC_PREFIX.length())).find()) {
                    topicInfos.add(this.getTopicInfo(key));
                }
            }
        } else {
            log.info("topics为空");
        }
        List<TopicInfo> paginated = PaginationUtils.paginate(topicInfos, start, length);
        Map<String, Object> map = new HashMap<>();
        map.put("recordsTotal", topicInfos.size());		// 总记录数
        map.put("recordsFiltered", paginated.size());	// 过滤后的总记录数
        map.put("data", paginated);  					// 分页列表
        return map;
    }

    @Override
    public List<GroupInfo> getTopicDetail(String topic) {
        StreamInfo.XInfoGroups infoGroups = LuaScriptUtil.getInfoGroups(Constants.TOPIC_PREFIX + topic, stringRedisTemplate);
        List<GroupInfo> groupInfos = new ArrayList<>();
        if (infoGroups != null) {
            for (StreamInfo.XInfoGroup infoGroup : infoGroups) {
                GroupInfo groupInfo = JSON.parseObject(JSON.toJSONString(infoGroup.getRaw()), GroupInfo.class);
                groupInfos.add(groupInfo);
            }
        }
        return groupInfos;
    }
}
