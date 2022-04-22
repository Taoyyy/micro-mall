package com.yuan.message.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MyRabbitConfig {
    //     =========================================聊天消息持久化到MYSQL==================================================================
    @Bean
    public Queue saveMessageToMysqlQueue() {
        return new Queue("message.mysql.queue", true, false, false);
    }

    @Bean
    public Exchange messageSaveExchange() {
        return new TopicExchange("message-save-exchange", true, false);
    }

    @Bean
    public Binding messageSaveToMysqlBinding() {
//        String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
        return new Binding("message.mysql.queue", Binding.DestinationType.QUEUE,
                "message-save-exchange", "message.save.message", null);
    }

    //     =========================================告诉MYSQL某条消息已签收==================================================================
    @Bean
    public Exchange messageSignExchange() {
        return new TopicExchange("message-signed-exchange", true, false);
    }


    @Bean
    public Queue signMessageToMysqlQueue() {
        return new Queue("message.signed.queue", true, false, false);
    }

    @Bean
    public Binding messageSignBinding() {
//        String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
        return new Binding("message.signed.queue", Binding.DestinationType.QUEUE,
                "message-signed-exchange", "message.signed.message", null);
    }

//     ===========================================================================================================

    //使用JSON序列化机制，进行消息转换
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}

