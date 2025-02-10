package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.exception.ConversationNotExistException;
import com.huyvu.lightmessage.repository.MessageRepo;
import com.huyvu.lightmessage.repository.MessageRepoImpl;
import com.huyvu.lightmessage.repository.MessageRepoImpl.MessageDto;
import com.huyvu.lightmessage.util.CursorPaging;
import com.huyvu.lightmessage.util.CursorPagingResult;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {
    private final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final MessageRepo msgRepo;
    private final RealtimeSendingService rtmService;


    public MessageServiceImpl(MessageRepo msgRepo, RealtimeSendingService rtmService) {
        this.msgRepo = msgRepo;
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

        var entity = MessageEntity.builder()
                .convId(request.convId())
                .content(request.content())
                .senderId(userId)
                .sentAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();


        msgRepo.updateMemberLastSendAt(entity.convId(), entity.sentAt());
        msgRepo.saveMessage(entity);
        rtmService.sendMessageNotification(request.convId(), entity);

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
        if(allConversations.size() < paging.cursor().limit()) {
            limit = paging.cursor().limit();
        }
        return CursorPagingResult.<MessageRepoImpl.ConversationDto, ConversationCursor>builder()
                .data(allConversations)
                .nextCursor(new ConversationCursor(limit))
                .build();
    }
}
