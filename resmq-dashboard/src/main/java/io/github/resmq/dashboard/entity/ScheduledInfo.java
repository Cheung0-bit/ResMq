package io.github.resmq.dashboard.entity;

import lombok.Data;

import java.util.List;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/2/2 15:07
 */
@Data
public class ScheduledInfo {

    private String scheduledName;

    private long messages;

    private List<ScheduledTask> scheduledTasks;

}
