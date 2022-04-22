package com.yuan.chat.util;

import com.alibaba.fastjson.JSONObject;
import com.yuan.mallapi.domain.chat.ChatMessage;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class MessageUtil {
    private static StringRedisTemplate redisTemplate;
    private static RabbitTemplate rabbitTemplate;
    @Value("${netty.port}")
    private String PORT;
    private static String port;

    @Autowired
    public void init(StringRedisTemplate redisTemplate, RabbitTemplate rabbitTemplate) {
        MessageUtil.redisTemplate = redisTemplate;
        MessageUtil.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void init2() {
        port = PORT;
    }

    /**
     * 上线一个用户
     *
     * @param channel
     * @param userId
     */
    public void online(Channel channel, String userId) throws UnknownHostException {
        //绑定channel和用户的id
        AttributeKey<String> key = AttributeKey.valueOf("user");
        channel.attr(key).set(userId);
        String ip4 = String.valueOf(Inet4Address.getLocalHost());
        String ip = ip4.substring(ip4.lastIndexOf("/") + 1);
        //redis中设置user到服务器的路由
        redisTemplate.opsForValue().set("chat:route:" + userId, ip + ":" + port);

        //TODO 从redis中取 chat:message:expired:"+uid，判断是否存在，存在说明该用户有过期离线消息，去MYSQL中查找
        //TODO 并且，不要直接连MYSQL查找，不然会增加MYSQL的压力，而且做不到异步
        boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey("chat:message:expired:" + userId));
        if (hasKey) {
            //告诉rabbitmq需要从Mysql拉取离线消息的用户id
            //message微服务监听mq中需要拉取离线消息的用户id,然后从mysql读取该用户的离线消息，然后发送回消息队列，该服务器监听消息队列，如收到
            // 离线消息则把消息发送给该用户
            rabbitTemplate.convertAndSend("message-pull-exchange", "message.users.pull.message", userId);
        }
        // 从redis中拉取离线消息
        Map<Object, Object> map = redisTemplate.opsForHash().entries("chat:message:" + userId);
        for (Object value : map.values()) {
            System.out.println(value);
            JSONObject chatMessage = JSONObject.parseObject((String) value);
            Long id = chatMessage.getLongValue("id");
            channel.writeAndFlush(new TextWebSocketFrame((String) value));
            //TODO 发送到消息队列告诉mysql该消息已签收
            System.out.println("发送到消息队列，签收消息" + id);
            rabbitTemplate.convertAndSend("message-signed-exchange", "message.signed.message", id);
        }
        //拉取完离线消息后从redis中删除消息
        redisTemplate.delete("chat:message:" + userId);
    }

    public void sendMessage(ChatMessage chatMessage, Channel toChannel) {
        Long userId = chatMessage.getReceiver();
        String msg = chatMessage.getMessage();
        String msgId = String.valueOf(chatMessage.getId());
        String chatMessageJson = JSONObject.toJSONString(chatMessage);
        //判断一下该用户是否连接的是本服务器
        if (toChannel != null) {
            //连接的是本服务器就直接发送就可以了
            toChannel.writeAndFlush(new TextWebSocketFrame(LocalDateTime.now() + ":     " + chatMessageJson));
            //消息设置为已接受，然后存入数据库
            chatMessage.setSigned(1);
            rabbitTemplate.convertAndSend("message-save-exchange", "message.save.message", chatMessage);
        } else {
            //判断一下该用户是否连接的是其他服务器，且在线
            boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey("chat:route:" + userId));
            if (hasKey) {
                //目标用户已上线且在其他服务器中，通过存在redis里的路由信息得知目标用户所在的服务器，通过mq转发到该服务器
                //发送到mq中，然后数据库mysql消费消息，把所有的消息都持久化
                rabbitTemplate.convertAndSend("message-save-exchange", "message.save.message", chatMessage);
                //TODO 每台服务器都创建并监听自己的队列，通过存在redis里的路由信息得知目标用户所在的服务器，发送到rabbitmq相应的队列中，让目标用户所在的服务器接收并转发消息


            } else {
                //目标用户已离线
                // 发送到redis中，该用户上线后自动拉取离线消息
                long currentTimeMillis = System.currentTimeMillis();
                HashMap<String, String> chatMessageHashMap = new HashMap<>();
                chatMessageHashMap.put(msgId, chatMessageJson);
                redisTemplate.opsForHash().putAll("chat:message:" + userId, chatMessageHashMap);
                //给离线消息设置过期时间,10天后自动过期，用户想再得到离线消息就要从MYSQL中取
                redisTemplate.expire("chat:message:" + userId, 2, TimeUnit.MINUTES);
                //发送到mq中，然后数据库mysql消费消息，把所有的消息都持久化
                rabbitTemplate.convertAndSend("message-save-exchange", "message.save.message", chatMessage);
            }
        }
    }


    public void remove(String userId) {
        //redis中删除该用户的路由
        redisTemplate.delete("chat:route:" + userId);
    }
}
