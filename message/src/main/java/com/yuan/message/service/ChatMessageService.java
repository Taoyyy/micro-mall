package com.yuan.message.service;

import com.yuan.mallapi.domain.chat.ChatMessage;

/**
 * @author 6
 * @description 针对表【chat_message_0】的数据库操作Service
 * @createDate 2022-04-17 14:25:25
 */
public interface ChatMessageService {

    void save(ChatMessage chatMessage);

    void signMessage(Long id);

    void pullAndSendMessage(Long uid);
}
