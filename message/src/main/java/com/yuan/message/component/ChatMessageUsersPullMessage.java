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
@RabbitListener(queues = "message.users.pull.message.queue")
public class ChatMessageUsersPullMessage {
    @Autowired
    private ChatMessageService messageService;

    //从mysql中拉取离线消息
    @RabbitHandler
    public void getUsersPullMessage(String id, Channel channel, Message message) {
        System.out.println("用户：" + id + "需要从mysql中拉取离线消息");
        Long uid = Long.valueOf(id);
        //TODO 存入mysql数据库
        try {
            //从mysql中拉取离线消息并发送回消息队列
            messageService.pullAndSendMessage(uid);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //TODO 把错误写入日志
            System.out.println("消息：" + id + "确认失败");
            System.out.println(e.getMessage());
        }
    }
}
