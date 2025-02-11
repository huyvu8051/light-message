package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.jpa.model.Conversation;
import com.huyvu.lightmessage.jpa.model.Member;
import com.huyvu.lightmessage.jpa.model.Message;
import com.huyvu.lightmessage.jpa.model.UserProfile;
import com.huyvu.lightmessage.jpa.repo.ConversationJpaRepo;
import com.huyvu.lightmessage.jpa.repo.MemberJpaRepo;
import com.huyvu.lightmessage.jpa.repo.MessageJpaRepo;
import com.huyvu.lightmessage.service.MessageService;
import com.huyvu.lightmessage.service.MessageService.MessageCursor;
import com.huyvu.lightmessage.util.CursorPaging;
import com.huyvu.lightmessage.util.CursorPagingResult;
import jakarta.persistence.EntityManager;
import lombok.Builder;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MessageRepoImpl implements MessageRepo {
    private final MemberJpaRepo memberJpaRepo;
    private final MessageJpaRepo messageJpaRepo;
    private final ConversationJpaRepo conversationJpaRepo;

    public MessageRepoImpl(MemberJpaRepo memberJpaRepo, MessageJpaRepo messageJpaRepo, ConversationJpaRepo conversationJpaRepo, EntityManager entityManager) {
        this.memberJpaRepo = memberJpaRepo;
        this.messageJpaRepo = messageJpaRepo;
        this.conversationJpaRepo = conversationJpaRepo;
    }

    //    @Cacheable(value = "findAllMessages")
    @Override
    public CursorPagingResult<MessageDTO, MessageCursor> findAllMessages(long convId, CursorPaging<MessageCursor> paging) {
        var allByConversationId = messageJpaRepo.findAllByConversationId(convId, paging.limit(), paging.cursor().sendAt(), paging.cursor().id());
        var collect = allByConversationId
                .stream()
                .map(m -> MessageDTO.builder()
                        .id(m.getId())
                        .content(m.getContent())
                        .senderId(m.getSender().getId())
                        .sendAt(m.getSendAt())
                        .build())
                .collect(Collectors.toList());

        var lastItem = collect.getLast();

        var cursor = new MessageCursor(lastItem.sendAt(), lastItem.id());

        return CursorPagingResult.<MessageDTO, MessageCursor>builder()
                .data(collect)
                .nextCursor(cursor)
                .build();
    }

    @Override
    public void saveMessage(MessageEntity msg) {
        var conv = Conversation.builder()
                .id(msg.convId())
                .build();
        var usr = UserProfile.builder()
                .id(msg.senderId())
                .build();
        var message = Message.builder()
                .conv(conv)
                .sender(usr)
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
    public void saveConversation(ConversationEntity conversation) {
        var entity = new Conversation();
        entity.setName(conversation.name());
        entity.setGroupChat(false);
        conversationJpaRepo.save(entity);
    }

    @Builder
    public record MessageDto(Long id,
                              String content,
                              OffsetDateTime sendAt) {
    }

    @Builder
    public record ConversationDto(
            long id,
            String name,
            boolean isGroupChat,
            MessageDto message
    ) {
        public ConversationDto(long id,
                               String name,
                               boolean isGroupChat,
                               Long messageId,
                               String messageContent,
                               Instant sendAt) {
            this(id, name, isGroupChat, new MessageDto(messageId, messageContent, sendAt != null ? sendAt.atOffset(ZoneOffset.UTC) : null));
        }
    }


    @Override
    public List<ConversationDto> findAllConversations(long userId, CursorPaging<MessageService.ConversationCursor> paging) {
        var newestConversation = conversationJpaRepo.findLatestConversation(userId, paging.cursor().limit());
        return newestConversation.stream().map(tuple -> new ConversationDto(tuple.get("conv_id", Long.class),
                tuple.get("name", String.class),
                tuple.get("is_group_chat", Boolean.class),
                tuple.get("m_id", Long.class),
                tuple.get("content", String.class),
                tuple.get("send_at", Instant.class))).toList();
    }


    /*
    SELECT conv_ids.conv_id AS conv_id
                , conv.name
                , conv.is_group_chat
                , m.id             AS m_id
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
                                ON conv.id = conv_ids.conv_id
     */

   /* @Override
    public List<ConversationDto> findAllConversations(long userId, Paging paging) {

        var convIds = conversationJpaRepo.findConversationsByUserId(userId);
        if (convIds.isEmpty()) {
            return new ArrayList<>();
        }

        var convDtos = new ArrayList<ConversationDto>(convIds.size());
        for (Conversation conv : convIds) {
            var msg = messageJpaRepo.findOneByConvIdOrderBySendAtDesc(conv.getId());
            var message = msg.map(m -> new MessageDto(m.getId(), m.getContent(), m.getSendAt()))
                    .orElse(null);
            convDtos.add(new ConversationDto(conv.getId(), conv.getName(), conv.isGroupChat(), message));
        }

        return convDtos;
    }*/

    @Override
    public Optional<Member> findMember(long userId, long convId) {
        return memberJpaRepo.findOneByConversationIdAndUserId(convId, userId);
    }

    @Override
    public void updateMemberLastSendAt(long convId, OffsetDateTime offsetDateTime) {
        memberJpaRepo.updateById(convId, offsetDateTime);
    }

    @Override
    public Optional<ConversationEntity> findConversation(long convId) {
        var convOpt = conversationJpaRepo.findById(convId);
        if (convOpt.isEmpty()) {
            return Optional.empty();
        }
        var conv = convOpt.get();
        return Optional.of(ConversationEntity.builder()
                .id(conv.getId())
                .name(conv.getName())
                .isGroupChat(conv.isGroupChat())
                .build());
    }

    @Override
    public Optional<MessageEntity> findConversationLastMessage(long convId) {
        var lastMsg = messageJpaRepo.findFirstByConvIdOrderBySendAtDesc(convId);
        if (lastMsg.isEmpty()) {
            return Optional.empty();
        }
        var msg = lastMsg.get();

        return Optional.of(MessageEntity.builder()
                .id(msg.getId())
                .convId(convId)
                .content(msg.getContent())
                .senderId(msg.getSender().getId())
                .sentAt(msg.getSendAt())
                .build());
    }

    @Override
    public List<Long> findAllMembers(long convId) {
        return memberJpaRepo.findAllByConversationId(convId);
    }


}
