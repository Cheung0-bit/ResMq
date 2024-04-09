package io.github.resmq.dashboard.service;

import io.github.resmq.dashboard.entity.ScheduledTask;

import java.util.List;
import java.util.Map;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/2/2 14:55
 */
public interface ScheduledService {

    List<ScheduledTask> getScheduledTasks(String scheduledName);

    Map<String, Object> returnScheduledTasks(int start, int length, String scheduledName);

}
