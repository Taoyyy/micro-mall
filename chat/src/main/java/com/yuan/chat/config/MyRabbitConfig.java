package com.yuan.chat.config;


import com.rabbitmq.client.Channel;
import com.yuan.chat.handler.WebSocketHandler;
import com.yuan.mallapi.domain.chat.ChatMessage;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class MyRabbitConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RabbitAdmin rabbitAdmin;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Value("${netty.port}")
    private int port;
    @Value("${netty.name}")
    private String name;
    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;
    @Value("${spring.rabbitmq.port}")
    private String rabbitPort;
    private final String IP = String.valueOf(Inet4Address.getLocalHost());
    private final String ip = IP.substring(IP.lastIndexOf("/") + 1);
    private final String serverAdd = ip + "." + port;
    private final String queue = "message.pull.message." + serverAdd + ".queue";


    @PostConstruct
    public void init() {
        String rabbitmqHost = rabbitHost + ":" + rabbitPort;
    }

    //配置连接工厂
    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setAddresses(rabbitHost);
        cachingConnectionFactory.setUsername("rabbit");
        cachingConnectionFactory.setPassword("rabbit");
        cachingConnectionFactory.setVirtualHost("/");
        return cachingConnectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {

        RabbitAdmin rabbitAdmin = new RabbitAdmin(cachingConnectionFactory());
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }


    public MyRabbitConfig() throws UnknownHostException {
    }


    @Bean
    public Exchange messagePullExchange() {
        TopicExchange exchange = new TopicExchange("message-pull-exchange", true, false);
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    public Queue messageUsersPullMessageQueue() {
        Queue queue = new Queue("message.users.pull.message.queue", true, false, false);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    @Bean
    public Queue messagePullMessageQueue() {
        Queue queue = new Queue("message.pull.message." + ip + "." + port + ".queue", true, false, false);
        //显式声明队列
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    @Bean
    public Binding messageUsersPullMessageBinding() {
        Binding binding = new Binding("message.users.pull.message.queue", Binding.DestinationType.QUEUE,
                "message-pull-exchange", "message.users.pull.message", null);
        rabbitAdmin.declareBinding(binding);
        return binding;
    }


    @Bean
    public Binding messagePullMessageBinding() {
        Binding binding = new Binding("message.pull.message." + ip + "." + port + ".queue", Binding.DestinationType.QUEUE,
                "message-pull-exchange", "message.pull.message." + ip + "." + port, null);
        rabbitAdmin.declareBinding(binding);
        return binding;
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
            //TODO 如果发送到broker失败，写入到日志里面
            System.out.println("拿到发送端确认回调");
        });
        //设置发送到指定队列的消息确认回调，失败后才会拿到这个回调
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            //TODO 把发送失败的消息写回到数据库
            System.out.println("消息" + returnedMessage.getMessage() + "发送到队列失败，建议写回数据库");
        });
    }

    //================================================ChatMessageReceiver=======================================================
    public static ConcurrentHashMap<String, io.netty.channel.Channel> channelMap = WebSocketHandler.channelMap;

    //
//接收从其他netty服务器或微服务转发到本服务器的消息，并转发给目标用户
    @RabbitListener(queues = "#{messagePullMessageQueue.name}")
    @RabbitHandler
    public void listener(@Payload ChatMessage chatMessage, Channel channel, Message message) {
        //TODO 从channelMap中查询到目标channel并转发
        System.out.println(chatMessage);
        String uid = String.valueOf(chatMessage.getReceiver());
        io.netty.channel.Channel channel1 = channelMap.get(uid);
        Long id = chatMessage.getId();
        if (channel1 != null) {
            try {
                channel1.writeAndFlush(new TextWebSocketFrame(LocalDateTime.now() + ":     " + chatMessage));
                //TODO 发送到消息队列告诉mysql该消息已签收
                rabbitTemplate.convertAndSend("message-signed-exchange", "message.signed.message", id);
                //TODO 删除redis中Mysql有未读消息标志
                String redisKey = "chat:message:expired:" + uid;
                System.out.println(redisKey);
                redisTemplate.delete(redisKey);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception e) {
                //TODO 把错误写入日志
                System.out.println("离线消息：" + chatMessage + "接收失败");
                System.out.println(e.getMessage());
            }
        }
    }
}

