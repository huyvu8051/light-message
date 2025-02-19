package com.huyvu.lightmessage;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String SOCKET_QUEUE = "socket-queue";
    public static final String MAIL_QUEUE = "mail-queue";

    public static final String NOTIFICATION_EXCHANGE = "notification-exchange";


    @Bean
    public Queue emailQueue() {
        return new Queue(MAIL_QUEUE, true);  // Durable Queue for email notifications
    }

    // Define socket queue
    @Bean
    public Queue socketQueue() {
        return new Queue(SOCKET_QUEUE, true); // Durable Queue for socket notifications
    }

    // Define topic exchange for notifications
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    // Bind emailQueue to the exchange with the routing key "notification.email"
    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(emailQueue)
                .to(notificationExchange)
                .with("notification.email");
    }

    // Bind socketQueue to the exchange with the routing key "notification.socket"
    @Bean
    public Binding socketBinding(Queue socketQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(socketQueue)
                .to(notificationExchange)
                .with("notification.socket");
    }


}


