package io.github.resmq.dashboard.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/2/2 18:45
 */
@Data
@AllArgsConstructor
public class DeadMessage {

    private String id;

    private String message;

}
