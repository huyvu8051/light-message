package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;

import java.util.List;
import java.util.Optional;

public interface MessageRepository {
    List<MessageEntity> findAllMessages(long convId);

    void saveMessage(MessageEntity message);

    long getNextMessageId();

    void updateConversationLastMessage(long convId, MessageEntity entity);

    Optional<ConversationEntity> getConversation(long id);

    long getNextConversationId();

    void saveConversation(ConversationEntity conversation);
}
