package io.github.resmq.dashboard.entity;

import lombok.Data;

import java.util.List;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/2/2 18:43
 */
@Data
public class DeadMessageSummary {

    private String originTopic;

    private List<DeadGroupMessage> deadGroupMessages;

}
