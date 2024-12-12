package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MessageRepositoryImpl implements MessageRepository {
    private final AtomicLong messageKeyGen = new AtomicLong(1_000_000);
    private final AtomicLong conversationKeyGen = new AtomicLong(2_000_000);
    private final Map<Long, MessageEntity> msgs = new ConcurrentHashMap<>();
    private final Map<Long, ConversationEntity> convs = new ConcurrentHashMap<>();

    @Override
    public List<MessageEntity> findAllMessages(long convId) {
        return msgs.values().stream()
                .filter(messageEntity -> messageEntity.convId() == convId)
                .sorted((o1, o2) -> Math.toIntExact(o2.timestamp() - o1.timestamp()))
                .toList();
    }

    @Override
    public void saveMessage(MessageEntity message) {
        msgs.put(message.id(), message);
    }

    @Override
    public long getNextMessageId() {
        return messageKeyGen.incrementAndGet();
    }


    /**
     * todo:
     * race condition in update conversation last update message
     * high traffic
     * large participant lists
     *
     * @param convId
     * @param entity
     */
    @Override
    public void updateConversationLastMessage(long convId, MessageEntity entity) {
        var conv = convs.get(convId);
        convs.put(convId, new ConversationEntity(conv.id(), conv.name(), conv.isGroupChat(), conv.createdAt(), entity.id(),entity.timestamp()));
    }

    @Override
    public Optional<ConversationEntity> getConversation(long id) {
        return Optional.of(convs.get(id));
    }

    @Override
    public long getNextConversationId() {
        return conversationKeyGen.incrementAndGet();
    }

    @Override
    public void saveConversation(ConversationEntity conversation) {
        convs.put(conversation.id(),conversation);
    }

    @Override
    public List<ConversationEntity> findAllConversations() {
        return convs.values().stream().toList();
    }
}
