package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RealtimeSendingServiceImpl implements RealtimeSendingService {
    private static final Logger logger = LoggerFactory.getLogger(RealtimeSendingServiceImpl.class);

    /**
     * todo: send new message notification to participants, including conversation id, name, last updated timestamp, last message content, last message sender.
     *
     * @param conversation
     * @param entity
     */
    @Override
    public void sendMessageNotification(ConversationEntity conversation, MessageEntity entity) {
        //logger.info("Send socket to channel {} = {}", conversation.id(), entity.content());
    }
}
