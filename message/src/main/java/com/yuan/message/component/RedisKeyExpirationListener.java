package com.yuan.message.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    @Autowired
    StringRedisTemplate redisTemplate;

    public RedisKeyExpirationListener(RedisMessageListenerContainer container) {
        super(container);
    }

    /**
     * Redis-key失效监听事件，所有key 失效都会走此方法
     *
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        //  获取失效的key
        String expiredKey = message.toString();
        //  消息过期后往redis存一个值，告诉该用户去MYSQL中查离线信息
        if (expiredKey.startsWith("chat:message:")) {
            String uid = expiredKey.substring(expiredKey.lastIndexOf(":") + 1);
            //表明该用户有过期的离线消息未读，要去查找数据库
            redisTemplate.opsForValue().set("chat:message:expired:" + uid, "1");
            System.out.println(expiredKey + "过期了");
        }
    }
}
