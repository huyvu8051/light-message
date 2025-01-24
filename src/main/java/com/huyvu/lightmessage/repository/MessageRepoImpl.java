package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.jpa.model.Conversation;
import com.huyvu.lightmessage.jpa.model.Member;
import com.huyvu.lightmessage.jpa.model.Message;
import com.huyvu.lightmessage.jpa.model.UserProfile;
import com.huyvu.lightmessage.jpa.repo.ConversationJpaRepo;
import com.huyvu.lightmessage.jpa.repo.MemberJpaRepo;
import com.huyvu.lightmessage.jpa.repo.MessageJpaRepo;
import com.huyvu.lightmessage.util.Paging;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Repository
public class MessageRepoImpl implements MessageRepo {
    private final AtomicLong conversationKeyGen = new AtomicLong(2_000_000);
    private final Map<Long, ConversationEntity> convs = new ConcurrentHashMap<>();
    private final MemberJpaRepo memberJpaRepo;
    private final MessageJpaRepo messageJpaRepo;
    private final ConversationJpaRepo conversationJpaRepo;

    public MessageRepoImpl(MemberJpaRepo memberJpaRepo, MessageJpaRepo messageJpaRepo, ConversationJpaRepo conversationJpaRepo, EntityManager entityManager) {
        this.memberJpaRepo = memberJpaRepo;
        this.messageJpaRepo = messageJpaRepo;
        this.conversationJpaRepo = conversationJpaRepo;
        var now = Instant.now().getEpochSecond();
        LongStream.range(2_000, 2_020).parallel().forEach(value -> {
            convs.put(value, new ConversationEntity(value, "Generated title", true));
        });
    }

//    @Cacheable(value = "findAllMessages")
    @Override
    public List<MessageEntity> findAllMessages(long convId, OffsetDateTime from, OffsetDateTime to) {
        var allByConversationId = messageJpaRepo.findAllByConversationId(convId);
        return allByConversationId.stream().map(m->MessageEntity.builder()
                .id(m.getId())
                .convId(m.getConv().getId())
                .content(m.getContent())
                .senderId(m.getSender().getId())
                .sentAt(m.getSendAt())
                .build())
                .collect(Collectors.toList());
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

        messageJpaRepo.save(message);
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

    private record MessageDto(long messageId,
                              String messageContent,
                              OffsetDateTime sendAt) {
    }

    public record ConversationDto(
            long id,
            String name,
            boolean isGroupChat,
            MessageDto message
    ) {
        public ConversationDto(long id,
                               String name,
                               boolean isGroupChat,
                               long messageId,
                               String messageContent,
                               Instant sendAt) {
            this(id, name, isGroupChat, new MessageDto(messageId, messageContent, sendAt.atOffset(ZoneOffset.UTC)));
        }
    }

    /*@Override
    public List<ConversationDto> findAllConversations(long userId, Paging paging) {

        String sql = """
                SELECT conv.id AS conv_id
                     , conv.name
                     , conv.is_group_chat
                     , m.id    AS m_id
                     , m.content
                     , m.send_at
                  FROM (SELECT conversation_id
                          FROM member
                         WHERE user_id = :userId) AS conv_ids(conv_id)
                           LEFT JOIN LATERAL (
                      SELECT *
                        FROM message
                       WHERE conv_id = conv_ids.conv_id
                       ORDER BY send_at DESC
                       LIMIT 1
                      ) m ON TRUE
                           LEFT JOIN conversation conv
                                     ON conv.id = m.conv_id""";


        var session = sessionFactory.getCurrentSession();
        return session.createNativeQuery(sql, Tuple.class)
                .setParameter("userId", userId)
                .setResultListTransformer(NativeQueryTupleTransformer.INSTANCE)
                .stream().map(tuple -> new ConversationDto(tuple.get("conv_id", Long.class),
                        tuple.get("name", String.class),
                        tuple.get("is_group_chat", Boolean.class),
                        tuple.get("m_id", Long.class),
                        tuple.get("content", String.class),
                        tuple.get("send_at", Long.class))).toList();
    }*/


    @Override
    public List<ConversationDto> findAllConversations(long userId, Paging paging) {
        var newestConversation = conversationJpaRepo.findNewestConversation(userId);
        return newestConversation.stream().map(tuple -> new ConversationDto(tuple.get("conv_id", Long.class),
                tuple.get("name", String.class),
                tuple.get("is_group_chat", Boolean.class),
                tuple.get("m_id", Long.class),
                tuple.get("content", String.class),
                tuple.get("send_at", Instant.class))).toList();
    }

    @Override
    public Optional<Member> findMember(long userId, long convId) {
        return memberJpaRepo.findOneByConversationIdAndUserId(convId, userId);
    }


}
