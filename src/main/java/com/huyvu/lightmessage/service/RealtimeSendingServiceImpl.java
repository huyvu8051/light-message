package com.huyvu.lightmessage.service;

import com.github.javafaker.Faker;
import com.huyvu.lightmessage.entity.MessageKafkaDTO;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.LongStream;

import static com.huyvu.lightmessage.RabbitConfig.*;


@Slf4j
@EnableScheduling
@Service
public class RealtimeSendingServiceImpl implements RealtimeSendingService {

    Faker faker = new Faker(Locale.of("vi"));

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


        rabbitTemplate.invoke(operations -> {
            entity.memberIds().forEach(member -> {
                operations.convertAndSend(NOTIFICATION_EXCHANGE, NOTIFICATION_SOCKET_ROUTING_KEY,
                        MessageRealtimeItemDTO.builder()
                                .id(entity.id())
                                .convId(entity.convId())
                                .content(entity.content())
                                .senderId(entity.senderId())
                                .sentAt(entity.sentAt())
                                .memberId(member)
                                .build()
                );
            });
            return null;
        });

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


    @Scheduled(fixedDelayString = "${gen-socket-scheduler.fixedDelay}")
    public void reportCurrentTime() {
        var convId = faker.number().numberBetween(1, 20);
        var mk = MessageKafkaDTO.builder()
                .id(faker.number().randomNumber())
                .content(faker.lorem().paragraph())
                .convId(convId)
                .sentAt(OffsetDateTime.now().toString())
                .memberIds(LongStream.range(1, 20).boxed().toList())
                .build();
        this.sendMessageNotification(convId, mk);
    }
}
