package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.entity.MessageKafkaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RealtimeSendingServiceImpl implements RealtimeSendingService {
    static final String TOPIC = "socket-message";

    private static final Logger logger = LoggerFactory.getLogger(RealtimeSendingServiceImpl.class);
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
        logger.info("Send socket to channel {} = {}", convId, entity);
        kafkaTemplate.send(TOPIC, entity);
    }
}
