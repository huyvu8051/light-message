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
//        SpringApplication.run(LightMessageApplication.class, args);




        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux.range(1, 5)

                .publishOn(Schedulers.boundedElastic())  // Đọc từ DB trên thread I/O
                .map(i -> {
                    System.out.println("Fetching from DB: " + i + " | Thread: " + Thread.currentThread().getName());
                    return i * 2;
                })
                .publishOn(Schedulers.parallel())  // Xử lý dữ liệu trên thread CPU
                .map(i -> {
                    System.out.println("Processing: " + i + " | Thread: " + Thread.currentThread().getName());
                    return i;
                })
                .doOnComplete(() -> countDownLatch.countDown())
                .subscribe();

        countDownLatch.await();

    }

    public void start() throws InterruptedException {
        // Boss handles incoming connections, Worker handles data transfer
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // Create Server Channel
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<Object>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
                                    // Handle incoming messages
                                    System.out.println("Received: " + msg);

                                }
                            });

                            ch.eventLoop().execute(() -> {});



                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections
            ChannelFuture future = bootstrap.bind(8088).sync();

            System.out.println("Netty server started on port " + 8088);

            // Wait until the server socket is closed
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

