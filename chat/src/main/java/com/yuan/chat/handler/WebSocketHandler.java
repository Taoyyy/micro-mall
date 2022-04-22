package com.yuan.chat.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuan.chat.util.MessageUtil;
import com.yuan.common.utils.JwtUtil;
import com.yuan.mallapi.domain.chat.ChatMessage;
import io.jsonwebtoken.Claims;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //定义一个channel组，管理所有的channel
    //GlobalEventExecutor是一个全局的事件执行器，是一个单例
    private static final DefaultChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    String DateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    //绑定用户id和channel
    public static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();
    private final MessageUtil messageUtil = new MessageUtil();
    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器端收到消息：" + msg.text());
        Channel channel = channelHandlerContext.channel();
        ChatMessage chatMessage = new ChatMessage();
        JSONObject json = JSON.parseObject(msg.text());
        Integer msgType = json.getInteger("msgType");
        String jwt = json.getString("msg");
        Long sender = json.getLong("sender");
        Long receiver = json.getLong("receiver");
        //初始化，绑定用户和channel，并注册到channelMap
        if (msgType == 1) {
            Claims claims = JwtUtil.parseJWT(jwt);
            String id = String.valueOf(claims.get("sub"));
            if (id != null) {
                messageUtil.online(channel, id);
                channelMap.put(id, channel);
            } else {
                System.out.println("token错误");
            }
        }
        //聊天消息
        else if (msgType == 2) {
            String message = json.getString("msg");
            chatMessage.setMsgType(2);
            chatMessage.setSender(sender);
            chatMessage.setReceiver(receiver);
            chatMessage.setMessage(message);
            //确认是本人发的消息，不是伪造的
            if (channelMap.get(String.valueOf(sender)) == channel) {
                //根据接收者的id获取接收者的channel
                Channel toChannel = getChannelByUserId(String.valueOf(receiver));
                messageUtil.sendMessage(chatMessage, toChannel);
            }
        }
    }

    //将当前channel加入到channelGroup
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        //将该客户加入聊天的信息推送给其他在线的客户端
        channelGroup.writeAndFlush("客户端：" + channel.remoteAddress() + "加入聊天" + DateNow);
        channelGroup.add(channel);
    }

    //用户离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
//        System.out.println("channel是："+ctx.channel());
        Channel channel = ctx.channel();
        //获取该channel的用户id
        AttributeKey<String> key = AttributeKey.valueOf("user");
        String userId = channel.attr(key).get();
        //redis中删除该用户的路由
        messageUtil.remove(userId);
        //channelMap中删除该用户
        channelMap.remove(userId);
        //redis中删除该用户
        String redisKey = "chat:route:" + userId;
        redisTemplate.delete(redisKey);
        System.out.println("handlerAdded被调用" + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("发生异常");
        System.out.println(cause);
        ctx.close();
    }

    /**
     * 根据用户id获取该用户的通道
     *
     * @param userId
     * @return
     */
    public Channel getChannelByUserId(String userId) {
        return channelMap.get(userId);
    }
}
