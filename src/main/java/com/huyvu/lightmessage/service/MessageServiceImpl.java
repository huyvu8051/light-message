package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.CursorPagingResponseDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.exception.ConversationNotExistException;
import com.huyvu.lightmessage.repository.MessageRepo;
import com.huyvu.lightmessage.repository.MessageRepoImpl;
import com.huyvu.lightmessage.util.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
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
    public CursorPagingResponseDTO<MessageDTO> getMessages(long userId, long convId, MessageCursorPaging paging) {
        checkUserIsMemberOfConversation(userId, convId);
        var allMessages = msgRepo.findAllMessages(convId, paging);
        var data = allMessages.stream()
                .skip(1)
                .limit(10)
                .map(e -> new MessageDTO(e.id(), e.content(), e.senderId(), e.sentAt())).toList();

        return CursorPagingResponseDTO.<MessageDTO>builder()
                .data(data)
                .build();
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
    public List<MessageRepoImpl.ConversationDto> getNewestConversations(long userId, Paging paging) {
        return msgRepo.findAllConversations(userId, paging);
    }
}
