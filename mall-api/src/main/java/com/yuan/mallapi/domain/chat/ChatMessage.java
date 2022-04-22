package com.yuan.mallapi.domain.chat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yuan.common.utils.SnowFlakeUtil;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "chat_message")
public class ChatMessage implements Serializable {
    /**
     * id
     */
    private Long id = SnowFlakeUtil.get().createId();
    /**
     * 发送者的id
     */
    private Long sender;
    /**
     * 接收者的id
     */
    private Long receiver;
    /**
     * 消息内容
     */
    private String message;

    /**
     * 消息类型
     */
    private int msgType;
    /**
     * 消息是否已签收
     */
    private int signed = 0;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
