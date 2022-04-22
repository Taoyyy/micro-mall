package com.yuan.chat.entity.vo;

public enum MsgType {
    BIND(1, "初始化连接,绑定channel"),
    CHAT(2, "聊天消息"),
    SIGNED(3, "消息签收");

    public final Integer type;
    public final String content;

    MsgType(Integer type, String content) {
        this.type = type;
        this.content = content;
    }
}
