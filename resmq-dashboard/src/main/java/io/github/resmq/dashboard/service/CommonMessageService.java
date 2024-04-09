package io.github.resmq.dashboard.service;

import java.util.Map;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/4/8 13:07
 */
public interface CommonMessageService {

    Map<String, Object> getCommonMessages(int start, int length, String topic);

}
