package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.jpa.ConversationDto;
import com.huyvu.lightmessage.jpa.model.Conversation;
import com.huyvu.lightmessage.jpa.model.Member;
import com.huyvu.lightmessage.jpa.model.Message;
import com.huyvu.lightmessage.jpa.model.UserProfile;
import com.huyvu.lightmessage.jpa.repo.ConversationJpaRepo;
import com.huyvu.lightmessage.jpa.repo.MemberJpaRepo;
import com.huyvu.lightmessage.jpa.repo.MessageJpaRepo;
import com.huyvu.lightmessage.util.Paging;
import org.hibernate.SessionFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;

@Repository
public class MessageRepoImpl implements MessageRepo {
    private final AtomicLong conversationKeyGen = new AtomicLong(2_000_000);
    private final Map<Long, ConversationEntity> convs = new ConcurrentHashMap<>();
    private final MemberJpaRepo memberJpaRepo;
    private final MessageJpaRepo messageJpaRepo;
    private final SessionFactory sessionFactory;
    private final ConversationJpaRepo conversationJpaRepo;

    public MessageRepoImpl(MemberJpaRepo memberJpaRepo, MessageJpaRepo messageJpaRepo, SessionFactory sessionFactory, ConversationJpaRepo conversationJpaRepo) {
        this.memberJpaRepo = memberJpaRepo;
        this.messageJpaRepo = messageJpaRepo;
        this.sessionFactory = sessionFactory;
        var now = Instant.now().getEpochSecond();
        LongStream.range(2_000, 2_020).parallel().forEach(value -> {
            convs.put(value, new ConversationEntity(value, "Generated title", true));
        });
        this.conversationJpaRepo = conversationJpaRepo;
    }

    @Cacheable(value = "findAllMessages")
    @Override
    public List<MessageEntity> findAllMessages(long convId) {

        return messageJpaRepo.findAllByConversationId(convId);
    }

    @Override
    public void saveMessage(MessageEntity msg) {
        var message = Message.builder()
                .conv(
                        Conversation.builder()
                                .id(msg.convId())
                                .build())
                .sender(UserProfile.builder()
                        .id(msg.senderId())
                        .build())
                .content(msg.content())
                .sendAt(msg.sentAt())
                .build();

        var save = messageJpaRepo.save(message);
        System.out.println(save);
    }


   /* @Override
    public void saveMessage(MessageEntity msg) {
        var session = sessionFactory.openSession();
        var transaction = session.beginTransaction();
        var conversation = session.getReference(Conversation.class, msg.convId());
        var sender = session.getReference(UserProfile.class, msg.senderId());
        var message = Message.builder()
                .conv(conversation)
                .sender(sender)
                .content(msg.content())
                .sendAt(msg.sentAt())
                .build();
        session.persist(message);
        transaction.commit();
        session.close();
    }*/

    @Override
    public long getNextConversationId() {
        return conversationKeyGen.incrementAndGet();
    }

    @Override
    public void saveConversation(ConversationEntity conversation) {
        convs.put(conversation.id(), conversation);
    }

    @Override
    public List<ConversationDto> findAllConversations(long userId, Paging paging) {
        return conversationJpaRepo.findAllByMemberId(userId);
    }

    @Override
    public Optional<Member> findMember(long userId, long convId) {
        return memberJpaRepo.findOneByUserIdAndConversationId(userId, convId);
    }
}
