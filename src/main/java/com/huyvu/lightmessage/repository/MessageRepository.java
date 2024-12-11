package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;

import java.util.List;

public interface MessageRepository {
    List<MessageEntity> findAllMessages(long convId);

    void saveMessage(MessageEntity message);

    long getNextMessageId();

    void updateConversationLastMessage(long convId, MessageEntity entity);

    ConversationEntity getConversation(long id);
}
