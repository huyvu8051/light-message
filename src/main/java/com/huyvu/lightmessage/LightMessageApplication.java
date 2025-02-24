package com.huyvu.lightmessage;

import com.huyvu.lightmessage.r2.R2MemberRepo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.util.concurrent.CountDownLatch;


@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class, ReactiveUserDetailsServiceAutoConfiguration.class})
@Import(DataSourceAutoConfiguration.class)

public class LightMessageApplication {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(LightMessageApplication.class, args);
    }

}

