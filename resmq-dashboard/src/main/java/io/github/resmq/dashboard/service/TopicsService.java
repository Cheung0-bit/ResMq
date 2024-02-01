package io.github.resmq.dashboard.service;

import io.github.resmq.dashboard.entity.GroupInfo;
import io.github.resmq.dashboard.entity.TopicInfo;

import java.util.List;

/**
 * <主题服务类>
 *
 * @Author zhanglin
 * @createTime 2024/2/1 13:13
 */
public interface TopicsService {

    TopicInfo getTopicInfo(String topic);

    List<TopicInfo> getAllTopicsInfo();

    List<GroupInfo> getTopicDetail(String topic);
}
