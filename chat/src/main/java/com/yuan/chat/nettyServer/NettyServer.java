package com.yuan.chat.nettyServer;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.yuan.chat.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.Inet4Address;
import java.net.UnknownHostException;

@Component
public class NettyServer {
    @Value("${netty.port}")
    private int port;
    @Value("${netty.name}")
    private String name;
    private final String IP = String.valueOf(Inet4Address.getLocalHost());
    private final String ip = IP.substring(IP.lastIndexOf("/") + 1);

    @Autowired
    NacosServiceManager nacosServiceManager;
    @Autowired
    NacosDiscoveryProperties nacosDiscoveryProperties;

    NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup();

    public NettyServer() throws UnknownHostException {
    }

    public void run() throws InterruptedException {


        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel channel) {
                            //获取到pipeline
                            channel.pipeline().addLast(new HttpServerCodec());
                            channel.pipeline().addLast(new ChunkedWriteHandler());
                            channel.pipeline().addLast(new HttpObjectAggregator(8192));
                            channel.pipeline().addLast(new WebSocketServerProtocolHandler("/chat"));
                            channel.pipeline().addLast(new WebSocketHandler());
                        }
                    });

            System.out.println("================服务器启动================");
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            System.out.println("================注册到nacos================");
            NamingService namingService = nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties());
            namingService.registerInstance(name, ip, port);
            channelFuture.channel().closeFuture().sync();
        } catch (NacosException ignored) {
            System.out.println("ip获取失败");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @PreDestroy
    public void destroy() throws NacosException {
        System.out.println("===============Netty服务关闭===============");
        System.out.println("================注销nacos================");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        NamingService namingService = nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties());
        namingService.deregisterInstance(name, ip, port);
    }


}
