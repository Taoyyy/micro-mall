package com.yuan.message.component;

import com.rabbitmq.client.Channel;
import com.yuan.mallapi.domain.chat.ChatMessage;
import com.yuan.message.service.ChatMessageService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "message.mysql.queue")
public class ChatMessageReceiver {
    @Autowired
    private ChatMessageService messageService;

    @Autowired
    public void init(ChatMessageService messageService) {
        this.messageService = messageService;
    }

    //聊天消息持久化到MYSQL
    @RabbitHandler
    public void listener(ChatMessage chatMessage, Channel channel, Message message) {
        System.out.println("mysql收到消息：" + chatMessage);
        //TODO 存入mysql数据库
        try {
            messageService.save(chatMessage);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //TODO 把错误写入日志
            System.out.println("消息：" + chatMessage + "写入数据库失败");
            System.out.println(e.getMessage());
        }
    }
}
