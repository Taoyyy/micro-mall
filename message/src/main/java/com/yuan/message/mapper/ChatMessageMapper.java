package com.yuan.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuan.mallapi.domain.chat.ChatMessage;

import java.util.List;

/**
 * @author 6
 * @description 针对表【chat_message_0】的数据库操作Mapper
 * @createDate 2022-04-17 14:25:25
 * @Entity com.yuan.message.entity.ChatMessage
 */
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    int insertSelective(ChatMessage chatMessage);

    int signMessage(Long id);

    List<ChatMessage> pullUnreceivedMessage(Long id);
}




