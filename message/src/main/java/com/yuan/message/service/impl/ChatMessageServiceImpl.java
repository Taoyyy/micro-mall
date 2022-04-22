package com.yuan.message.service.impl;


import com.yuan.mallapi.domain.chat.ChatMessage;
import com.yuan.message.mapper.ChatMessageMapper;
import com.yuan.message.service.ChatMessageService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 6
 * @description 针对表【chat_message_0】的数据库操作Service实现
 * @createDate 2022-04-17 14:25:25
 */
@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    @Resource
    private ChatMessageMapper chatMessageMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void save(ChatMessage chatMessage) {
        chatMessageMapper.insert(chatMessage);
    }

    @Override
    public void signMessage(Long id) {
        chatMessageMapper.signMessage(id);
    }

    @Override
    public void pullAndSendMessage(Long uid) {
        //从mysql中拉取用户的离线消息
        List<ChatMessage> chatMessages = chatMessageMapper.pullUnreceivedMessage(uid);
        String id = String.valueOf(uid);
        String redisKey = "chat:route:" + id;
        String serverAddress = redisTemplate.opsForValue().get(redisKey);
        if (serverAddress != null) {
            String serverAdd = serverAddress.replace(":", ".");
            //将该离线消息发送回该用户所在的netty服务器所监听的消息队列
            for (ChatMessage chatMessage : chatMessages
            ) {
                rabbitTemplate.convertAndSend("message-pull-exchange", "message.pull.message." + serverAdd, chatMessage);
            }

        }
    }
}




