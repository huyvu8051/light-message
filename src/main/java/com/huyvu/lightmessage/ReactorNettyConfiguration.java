package com.huyvu.lightmessage;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReactorNettyConfiguration {
    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.addServerCustomizers(httpServer -> {
            int numberOfThreads = 6; // Set your desired number of threads here
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup(numberOfThreads);
            return httpServer.runOn(eventLoopGroup);
        });
        return factory;
    }
}