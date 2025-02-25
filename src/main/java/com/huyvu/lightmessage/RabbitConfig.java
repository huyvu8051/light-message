package com.huyvu.lightmessage;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String SOCKET_BROADCAST_QUEUE = "socket-broadcast-queue";
    public static final String NOTIFICATION_EXCHANGE = "notification-exchange";
    public static final String NOTIFICATION_SOCKET_ROUTING_KEY = "notification.socket";


    // Define socket queue
    @Bean
    public Queue socketQueue() {
        return new Queue(SOCKET_BROADCAST_QUEUE, true); // Durable Queue for socket notifications
    }

    // Define topic exchange for notifications
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }


    // Bind socketQueue to the exchange with the routing key "notification.socket"
    @Bean
    public Binding socketBinding(Queue socketQueue, TopicExchange notificationExchange) {
        return BindingBuilder.bind(socketQueue)
                .to(notificationExchange)
                .with(NOTIFICATION_SOCKET_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}


