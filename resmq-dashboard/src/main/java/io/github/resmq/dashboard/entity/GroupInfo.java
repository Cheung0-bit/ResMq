package io.github.resmq.dashboard.entity;

import lombok.Data;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/2/1 20:58
 */
@Data
public class GroupInfo {

    private String name;

    private long consumers;

    private long pending;

    private String lastDeliveredId;

}
