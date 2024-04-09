package io.github.resmq.dashboard.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/4/9 13:09
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeadMessage extends CommonMessage {

    private String originTopic;

    private String groupName;

    public DeadMessage(String id, String message, String originTopic, String groupName) {
        super(id, message);
        this.originTopic = originTopic;
        this.groupName = groupName;
    }
}
