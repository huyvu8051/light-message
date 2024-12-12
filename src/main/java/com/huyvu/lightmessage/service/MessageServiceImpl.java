package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.exception.ConversationNotExistException;
import com.huyvu.lightmessage.repository.MessageRepository;
import com.huyvu.lightmessage.security.UserContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final MessageRepository msgRepo;
    private final RealtimeMessagingService rtmService;
    private final UserContextProvider userContextProvider;


    public MessageServiceImpl(MessageRepository msgRepo, RealtimeMessagingService rtmService, UserContextProvider userContextProvider) {
        this.msgRepo = msgRepo;
        this.rtmService = rtmService;
        this.userContextProvider = userContextProvider;
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
    public MessageEntity sendMessage(SendMessageRequestDTO request) {
        var conversationOpt = msgRepo.getConversation(request.convId());
        if (conversationOpt.isEmpty()) {
            throw new ConversationNotExistException("ID: " + request.convId());
        }
        var conversation = conversationOpt.get();
        var now = Instant.now();
        var id = msgRepo.getNextMessageId();

        var entity = new MessageEntity(id, request.convId(), request.content(), userContextProvider.getUserContext().id(), now.getEpochSecond());

        msgRepo.saveMessage(entity);
        msgRepo.updateConversationLastMessage(request.convId(), entity);

        rtmService.sendMessageNotification(conversation, entity);

        logger.info("User {} send a message to {}", userContextProvider.getUserContext().id(), request.convId());
        return entity;
    }

    @Override
    public List<MessageDTO> getMessages(long convId) {
        var allMessages = msgRepo.findAllMessages(convId);
        return allMessages.stream().map(e -> new MessageDTO(e.id(), e.content(), e.senderId())).toList();
    }

    @Override
    public ConversationEntity createGroupChatConversation(CreateConversationRequestDTO request) {

        var id = msgRepo.getNextConversationId();
        var conversation = new ConversationEntity(id, request.conversationName(), true, Instant.now().getEpochSecond(), 0, 0);
        msgRepo.saveConversation(conversation);
        return conversation;
    }

    @Override
    public List<ConversationEntity> getAllConversations() {
        return msgRepo.findAllConversations();
    }
}
