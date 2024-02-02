package io.github.resmq.dashboard.service;

import io.github.resmq.dashboard.entity.DeadMessageSummary;

import java.util.List;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/2/2 18:33
 */
public interface DeadMessageService {

    List<DeadMessageSummary> getDMSummary();

}
