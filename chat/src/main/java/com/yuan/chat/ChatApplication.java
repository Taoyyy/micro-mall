package com.yuan.chat;

import com.yuan.chat.nettyServer.NettyServer;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

@EnableDiscoveryClient
@SpringBootApplication
@EnableRabbit
public class ChatApplication {
    @Autowired
    NettyServer nettyServer;


    public static void main(String[] args) throws InterruptedException {

        // 启动嵌入式的 Tomcat 并初始化 Spring 环境及其各 Spring 组件
        //将netty服务器放在web容器中运行
        ApplicationContext context = SpringApplication.run(ChatApplication.class, args);
        NettyServer nettyServer = context.getBean(NettyServer.class);
        nettyServer.run();

    }

}
