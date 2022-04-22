package com.yuan.message.component;

import com.rabbitmq.client.Channel;
import com.yuan.message.service.ChatMessageService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "message.signed.queue")
public class ChatMessageSigned {
    @Autowired
    private ChatMessageService messageService;

    @Autowired
    public void init(ChatMessageService messageService) {
        this.messageService = messageService;
    }

    //确认消息已签收
    @RabbitHandler
    public void signMessage(Long id, Channel channel, Message message) {
        System.out.println("mysql中消息：" + id + "已确认签收");
        //TODO 存入mysql数据库
        try {
            messageService.signMessage(id);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //TODO 把错误写入日志
            System.out.println("消息：" + id + "确认失败");
            System.out.println(e.getMessage());
        }
    }
}
