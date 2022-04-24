package com.yuan.order.component;

import com.rabbitmq.client.Channel;
import com.yuan.order.entity.Order;
import com.yuan.order.mapper.OrderMapper;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
@RabbitListener(queues = "order.save.order.queue")
public class SaveOrderReceiver {
    @Resource
    OrderMapper orderMapper;

    @RabbitHandler
    public void listener(Order order, Channel channel, Message message) throws IOException {
        orderMapper.insertSelective(order);
        System.out.println(order);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
