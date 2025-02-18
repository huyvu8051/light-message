package com.huyvu.lightmessage.service;

import com.github.javafaker.Faker;
import com.huyvu.lightmessage.entity.MessageKafkaDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.stream.LongStream;


@Slf4j
@EnableScheduling
@Service
public class RealtimeSendingServiceImpl implements RealtimeSendingService {
    static final String TOPIC = "socket-message";

    Faker faker = new Faker(Locale.of("vi"));

    private final KafkaTemplate<String, MessageKafkaDTO> kafkaTemplate;

    public RealtimeSendingServiceImpl(KafkaTemplate<String, MessageKafkaDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * todo: send new message notification to participants, including conversation username, name, last updated sentAt, last message content, last message sender.
     *
     * @param
     * @param entity
     */
    @Override
    public void sendMessageNotification(long convId, MessageKafkaDTO entity) {
        log.info("Produce kafka {} = {}", convId, entity.content().subSequence(0, 10) + "...");
        log.info("Produce kafka {} = {}", convId, entity.content().subSequence(0, 10) + "...");
        kafkaTemplate.send(TOPIC, entity);
    }


    @Scheduled(fixedDelay  = 50)
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
