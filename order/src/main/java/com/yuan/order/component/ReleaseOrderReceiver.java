package com.yuan.order.component;

import com.rabbitmq.client.Channel;
import com.yuan.order.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "order.release.order.queue")
public class ReleaseOrderReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseOrderReceiver.class);

    @RabbitHandler
    public void listener(Order order, Channel channel, Message message) throws IOException {
        LOGGER.info("收到过期订单，准备关闭订单：" + order.getOrderSn());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
