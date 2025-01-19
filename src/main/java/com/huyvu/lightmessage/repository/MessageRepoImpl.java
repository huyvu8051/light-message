package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.jpa.model.Member;
import com.huyvu.lightmessage.jpa.repo.MemberJpaRepo;
import com.huyvu.lightmessage.jpa.repo.MessageJpaRepo;
import com.huyvu.lightmessage.util.Paging;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

@Repository
public class MessageRepoImpl implements MessageRepo {
    private final AtomicLong messageKeyGen = new AtomicLong(1_000_000);
    private final AtomicLong conversationKeyGen = new AtomicLong(2_000_000);
    private final Map<Long, MessageEntity> msgs = new ConcurrentHashMap<>();
    private final Map<Long, ConversationEntity> convs = new ConcurrentHashMap<>();
    private final MemberJpaRepo memberJpaRepo;
    private final MessageJpaRepo messageJpaRepo;
    public MessageRepoImpl(MemberJpaRepo memberJpaRepo, MessageJpaRepo messageJpaRepo) {
        this.memberJpaRepo = memberJpaRepo;
        this.messageJpaRepo = messageJpaRepo;
        var now = Instant.now().getEpochSecond();
        LongStream.range(2_000, 2_020).parallel().forEach(value -> {
            convs.put(value, new ConversationEntity(value, "Generated title", true, now, now, now));
        });
    }

    @Cacheable(value = "findAllMessages")
    @Override
    public List<MessageEntity> findAllMessages(long convId) {
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return msgs.values().stream()
                .filter(messageEntity -> messageEntity.convId() == convId)
                .sorted((o1, o2) -> Math.toIntExact(o2.sentAt() - o1.sentAt()))
                .toList();
    }

    @Override
    public void saveMessage(MessageEntity message) {
        var entity = MessageJpaRepo.SendMessage.builder()
                .senderId(message.senderId())
                .convId(message.convId())
                .content(message.content())
                .sendAt(message.sentAt())
                .build();

        messageJpaRepo.saveNew(entity);
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
    public List<ConversationEntity> findAllConversations(long userId, Paging paging) {
        return convs.values().stream().toList();
    }

    @Override
    public Optional<Member> findMember(long userId, long convId) {
        return memberJpaRepo.findOneByUserIdAndConversationId(userId, convId);
    }
}
