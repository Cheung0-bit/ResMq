package io.github.resmq.dashboard.entity;

import lombok.Data;

/**
 * <主题信息>
 *
 * @Author zhanglin
 * @createTime 2024/2/1 19:43
 */
@Data
public class TopicInfo {

    private String name;

    private long length;

    private long radixTreeKeys;

    private long radixTreeNodes;

    private long groups;

    private String lastGeneratedId;

}
