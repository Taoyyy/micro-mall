package com.yuan.chat.service.impl;

import com.yuan.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private LoadBalancerClient loadBalancerClient;
    @Value("${netty.name}")
    private String serverName;


    @Override
    public String findNettyServer(Long id) {
        ServiceInstance instance = loadBalancerClient.choose(serverName);
        String host = instance.getHost();
        int port = instance.getPort();
        return host + ":" + port;
    }
}
