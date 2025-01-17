package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

@Repository
public class MessageRepositoryImpl implements MessageRepository {
    private final AtomicLong messageKeyGen = new AtomicLong(1_000_000);
    private final AtomicLong conversationKeyGen = new AtomicLong(2_000_000);
    private final Map<Long, MessageEntity> msgs = new ConcurrentHashMap<>();
    private final Map<Long, ConversationEntity> convs = new ConcurrentHashMap<>();

    public MessageRepositoryImpl() {
        var now = Instant.now().getEpochSecond();
        LongStream.range(2_000, 2_020).parallel().forEach(value -> {
            convs.put(value, new ConversationEntity(value, "Generated title", true, now, now, now));
        });
    }

    @Cacheable(value = "findAllMessages", condition = "#convId > 5")
    @Override
    public List<MessageEntity> findAllMessages(long convId) {
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return msgs.values().stream()
                .filter(messageEntity -> messageEntity.convId() == convId)
                .sorted((o1, o2) -> Math.toIntExact(o2.timestamp() - o1.timestamp()))
                .toList();
    }

    @Override
    public void saveMessage(MessageEntity message) {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        convs.put(convId, new ConversationEntity(conv.id(), conv.name(), conv.isGroupChat(), conv.createdAt(), entity.id(), entity.timestamp()));
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ConversationEntity> getConversation(long id) {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        var value = convs.get(id);

        return Optional.of(value);
    }

    @Override
    public long getNextConversationId() {
        return conversationKeyGen.incrementAndGet();
    }

    @Override
    public void saveConversation(ConversationEntity conversation) {
        convs.put(conversation.id(), conversation);
    }

    @Override
    public List<ConversationEntity> findAllConversations() {
        return convs.values().stream().toList();
    }
}
