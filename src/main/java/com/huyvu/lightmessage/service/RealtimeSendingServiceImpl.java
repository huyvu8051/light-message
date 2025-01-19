package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.entity.MessageEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RealtimeSendingServiceImpl implements RealtimeSendingService {
    private static final Logger logger = LoggerFactory.getLogger(RealtimeSendingServiceImpl.class);

    /**
     * todo: send new message notification to participants, including conversation username, name, last updated sentAt, last message content, last message sender.
     *
     * @param conversation
     * @param entity
     */
    @Override
    public void sendMessageNotification(long convId, MessageEntity entity) {
        //logger.info("Send socket to channel {} = {}", conversation.username(), entity.content());
    }
}
