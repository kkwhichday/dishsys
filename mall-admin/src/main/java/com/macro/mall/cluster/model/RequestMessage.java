package com.macro.mall.cluster.model;

import lombok.Data;

/**
 *  浏览器向服务端请求的消息
 */
@Data
public class RequestMessage {
    private String clientMsgId;
    private String fromUserName;
    private String toUserName;
    private String content;
}
