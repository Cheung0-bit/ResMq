package io.github.resmq.dashboard.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/2/2 15:19
 */
@Data
public class ScheduledTask {

    private String scheduledName;

    private String message;

    private LocalDateTime deadline;

    private boolean isExpired;

}
