package io.github.resmq.dashboard.service;

import io.github.resmq.dashboard.entity.CommonMessageSummary;

import java.util.List;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/2/2 21:53
 */
public interface PendingService {

    List<CommonMessageSummary> getPendingSummary();


}
