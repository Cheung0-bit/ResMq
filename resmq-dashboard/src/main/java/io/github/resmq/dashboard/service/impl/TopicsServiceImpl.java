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
        map.put("recordsTotal", topicInfos.size());             // 总记录数
        map.put("recordsFiltered", topicInfos.size());           // 过滤后的总记录数
        map.put("data", paginated);                             // 分页列表
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

    @Override
    public String topicDetailHtml(String topic) {
        List<GroupInfo> groupInfos = getTopicDetail(topic);
        StringBuilder html = new StringBuilder("<style>");
        html.append("table.hacker-table {");
        html.append("  border-collapse: collapse;");
        html.append("  width: 100%;");
        html.append("}");
        html.append("table.hacker-table th, table.hacker-table td {");
        html.append("  padding: 8px;");
        html.append("  text-align: left;");
        html.append("  border-bottom: 1px solid #ddd;");
        html.append("}");
        html.append("table.hacker-table th {");
        html.append("  background-color: green;");
        html.append("}");
        html.append("</style>");
        html.append("<table class=\"hacker-table\">");
        html.append("<tr>");
        html.append("<th>Name</th>");
        html.append("<th>Consumers</th>");
        html.append("<th>Pending</th>");
        html.append("<th>Last Delivered ID</th>");
        html.append("</tr>");
        for (GroupInfo groupInfo : groupInfos) {
            html.append("<tr>");
            html.append("<td>").append(groupInfo.getName()).append("</td>");
            html.append("<td>").append(groupInfo.getConsumers()).append("</td>");
            html.append("<td>").append(groupInfo.getPending()).append("</td>");
            html.append("<td>").append(groupInfo.getLastDeliveredId()).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        return html.toString();
    }
}
