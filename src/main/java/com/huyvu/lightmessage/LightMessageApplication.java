package com.huyvu.lightmessage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class, ReactiveUserDetailsServiceAutoConfiguration.class})
@Import(DataSourceAutoConfiguration.class)
public class LightMessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(LightMessageApplication.class, args);
    }
}

