package io.github.resmq.dashboard.service;

import io.github.resmq.dashboard.entity.ScheduledInfo;
import io.github.resmq.dashboard.entity.ScheduledTask;

import java.util.List;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/2/2 14:55
 */
public interface ScheduledService {

    List<ScheduledInfo> getScheduledMessages();

    List<ScheduledTask> getScheduledTasks(String key);

}
