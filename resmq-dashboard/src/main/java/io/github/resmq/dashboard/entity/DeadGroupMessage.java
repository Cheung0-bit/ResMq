package io.github.resmq.dashboard.entity;

import lombok.Data;

import java.util.List;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/2/2 19:19
 */
@Data
public class DeadGroupMessage {

    private String groupName;

    private List<DeadMessage> deadMessages;

}
