package com.yuan.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyRabbitConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;

    //设置该队列消息的过期时间，并设置死信交换机。该队列的消息过期后将通过死信交换机发往死信队列
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 20000);
        Queue queue = new Queue("order.delay.queue", true, false, false, arguments);
        return queue;
    }

    //死信队列
    @Bean
    public Queue orderReleaseOrderQueue() {
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }

    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange("order-event-exchange", true, false);
    }

    @Bean
    public Binding orderCreatOrderBinding() {
//        String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
        return new Binding("order.delay.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.create.order", null);
    }

    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding("order.release.order.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.release.order", null);
    }

    //==============================================将订单保存到mysql=======================================================
    //该队列中的消息将被保存到mysql
    @Bean
    public Queue orderSaveQueue() {
        Queue queue = new Queue("order.save.order.queue", true, false, false);
        return queue;
    }

    @Bean
    public Binding orderSaveOrderBinding() {
        return new Binding("order.save.order.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.save.order", null);
    }

//     ===========================================================================================================

    //使用JSON序列化机制，进行消息转换
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //定制rabbitTemplate
    //创建完Config对象后再执行该方法
    //correlationData发送消息的唯一关联数据，类似ID
    @PostConstruct
    public void initRabbitTemplate() {
        //设置发送到Broker消息确认回调，成功还是失败
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//                System.out.println("拿到发送端确认回调");
//                System.out.println("=======================");
        });
        //设置发送到指定队列的消息确认回调，失败后才会拿到这个回调
        rabbitTemplate.setReturnsCallback(returnedMessage -> System.out.println("消息" + returnedMessage.getMessage() + "发送到队列失败，建议写回数据库"));
    }


}

