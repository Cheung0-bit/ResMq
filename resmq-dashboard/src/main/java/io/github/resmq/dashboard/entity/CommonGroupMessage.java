package io.github.resmq.dashboard.entity;

import lombok.Data;

import java.util.List;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/2/2 19:19
 */
@Data
public class CommonGroupMessage {

    private String groupName;

    private List<CommonMessage> commonMessages;

}
