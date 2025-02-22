package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.entity.MessageKafkaDTO;
import com.huyvu.lightmessage.exception.ConversationNotExistException;
import com.huyvu.lightmessage.jpa.model.Member;
import com.huyvu.lightmessage.repository.MessageRepo;
import com.huyvu.lightmessage.repository.MessageRepoImpl;
import com.huyvu.lightmessage.repository.MessageRepoImpl.MessageDto;
import com.huyvu.lightmessage.repository.MessageRepoR2;
import com.huyvu.lightmessage.util.CursorPaging;
import com.huyvu.lightmessage.util.CursorPagingResult;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {
    private final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final MessageRepo msgRepo;
    private final MessageRepoR2 msgRepoR2;
    private final RealtimeSendingService rtmService;


    public MessageServiceImpl(MessageRepo msgRepo, MessageRepoR2 msgRepoR2, RealtimeSendingService rtmService) {
        this.msgRepo = msgRepo;
        this.msgRepoR2 = msgRepoR2;
        this.rtmService = rtmService;
    }


    /**
     * todo:
     * cache
     * cache update
     * transaction in save message and update last message in conversation
     * send notification and realtime message into recipients
     * handle media upload
     * message validation
     * audit logging
     * message delivery status updates
     * rate limit
     * send analytics data (monitoring )
     * error handling/retry
     *
     * @param request SendMessageRequest
     * @return
     */
    @Override
    public void sendMessage(long userId, SendMessageRequestDTO request) {
        checkUserIsMemberOfConversation(userId, request.convId());

        List<Long> ids = msgRepo.findAllMembers(request.convId());
        var entity = MessageEntity.builder()
                .convId(request.convId())
                .content(request.content())
                .senderId(userId)
                .sentAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();


        msgRepo.updateMemberLastSendAt(entity.convId(), entity.sentAt());
        var msgId = msgRepo.saveMessage(entity);

        rtmService.sendMessageNotification(request.convId(), MessageKafkaDTO.builder()
                .id(msgId)
                .convId(request.convId())
                .content(request.content())
                .senderId(userId)
                .sentAt(entity.sentAt().toString())
                .memberIds(ids)
                .build());

        // logger.info("User {} send a message to {}", userContextProvider.getUserContext().username(), request.convId());

    }

    @Override
    public MessageRepoImpl.ConversationDto getNewestConversation(long userId, long convId) {
        msgRepo.findMember(userId, convId).orElseThrow(() -> new ConversationNotExistException("User not a member or conversation not exist"));
        var conv = msgRepo.findConversation(convId).orElseThrow(() -> new ConversationNotExistException("Conversation not exist"));

        @Nullable
        var lastMsg = msgRepo.findConversationLastMessage(convId).map(messageEntity -> MessageDto.builder()
                        .id(messageEntity.id())
                        .content(messageEntity.content())
                        .sendAt(messageEntity.sentAt())
                        .build())
                .orElse(null);

        return MessageRepoImpl.ConversationDto.builder()
                .id(conv.id())
                .name(conv.name())
                .isGroupChat(conv.isGroupChat())
                .message(lastMsg)
                .build();
    }


    @Override
    public CursorPagingResult<MessageDTO, MessageCursor> getMessages(long userId, long convId, CursorPaging<MessageCursor> paging) {
        checkUserIsMemberOfConversation(userId, convId);
        return msgRepo.findAllMessages(convId, paging);

    }

    private void checkUserIsMemberOfConversation(long userId, long convId) {
        Optional<?> conversation = msgRepo.findMember(userId, convId);
        if (conversation.isEmpty()) {
            throw new ConversationNotExistException("userId: " + userId + " convId:" + convId);
        }
    }

    @Override
    public ConversationEntity createGroupChatConversation(long userId, CreateConversationRequestDTO request) {
        var conversation = new ConversationEntity(0l, request.conversationName(), true);
        msgRepo.saveConversation(conversation);
        return conversation;
    }

    @Override
    public CursorPagingResult<MessageRepoImpl.ConversationDto, ConversationCursor> getNewestConversation(long userId, CursorPaging<ConversationCursor> paging) {
        var allConversations = msgRepo.findAllConversations(userId, paging);

        var limit = paging.cursor().limit() + paging.limit();
        if (allConversations.size() < paging.cursor().limit()) {
            limit = paging.cursor().limit();
        }
        return CursorPagingResult.<MessageRepoImpl.ConversationDto, ConversationCursor>builder()
                .data(allConversations)
                .nextCursor(new ConversationCursor(limit))
                .build();
    }

    @Override
    public Mono<CursorPagingResult<MessageDTO, MessageCursor>> getMessagesR2(long userId, long convId, CursorPaging<MessageCursor> paging) {
       return checkUserIsMemberOfConversationR2(userId, convId)
               .then(Mono.defer(() -> {
                   Mono<List<MessageDTO>> msg = msgRepoR2.findAllMessages(convId, paging).collectList();

                   return msg.map(collect -> {
                       var lastItem = collect.getLast();

                       var cursor = new MessageCursor(lastItem.sendAt(), lastItem.id());

                       return CursorPagingResult.<MessageDTO, MessageCursor>builder()
                               .data(collect)
                               .nextCursor(cursor)
                               .build();
                   });

               }));
    }

    private Mono<Void> checkUserIsMemberOfConversationR2(long userId, long convId) {
        Mono<Optional<Member>> mem = msgRepoR2.findMember(userId, convId);
        return mem.flatMap(o -> {
            if (o.isEmpty()) {
                return Mono.error(new ConversationNotExistException("User not a member or conversation not exist"));
            }
            return Mono.empty();
        });
    }
}
