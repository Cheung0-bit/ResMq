package io.github.resmq.dashboard.entity;

import lombok.Data;

import java.util.List;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/2/2 18:43
 */
@Data
public class CommonMessageSummary {

    private String originTopic;

    private List<CommonGroupMessage> commonGroupMessages;

}
