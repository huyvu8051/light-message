package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.entity.MessageKafkaDTO;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.io.Serializable;

import static com.huyvu.lightmessage.RabbitConfig.NOTIFICATION_EXCHANGE;
import static com.huyvu.lightmessage.RabbitConfig.NOTIFICATION_SOCKET_ROUTING_KEY;


@Slf4j
@EnableScheduling
@Service
public class RealtimeSendingServiceImpl implements RealtimeSendingService {


    private final RabbitTemplate rabbitTemplate;

    public RealtimeSendingServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * todo: send new message notification to participants, including conversation username, name, last updated sentAt, last message content, last message sender.
     *
     * @param
     * @param entity
     */
    @Override
    public void sendMessageNotification(long convId, MessageKafkaDTO entity) {
        log.info("Produce notify {} = {}", convId, entity.content().subSequence(0, 10) + "...");
        rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, NOTIFICATION_SOCKET_ROUTING_KEY, entity);
    }


    @Builder
    public record MessageRealtimeItemDTO(
            long id,
            long convId,
            String content,
            long senderId,
            String sentAt,
            long memberId
    ) implements Serializable {
    }


}
