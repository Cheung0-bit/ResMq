package io.github.resmq.dashboard.entity;

import lombok.Data;

/**
 * <>
 *
 * @author zhanglin
 * @createTime 2024/2/2 18:45
 */
@Data
public class CommonMessage {

    //todo 消息优先级设计？ timestamp方式？ 路由匹配？ 权重？

    private String id;

    private String message;

    public CommonMessage() {

    }

    public CommonMessage(String id, String message) {
        this.id = id;
        this.message = message;
    }
}
