package io.github.resmq.dashboard.service;

import java.util.Map;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/2/2 18:33
 */
public interface DeadMessageService {

    Map<String, Object> getDeadMessages(int start, int length, String topic);

}
