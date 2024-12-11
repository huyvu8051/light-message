package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequest;
import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.repository.MessageRepository;
import com.huyvu.lightmessage.security.UserContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ParticipantServiceImpl implements ParticipantService {
    private final Logger logger = LoggerFactory.getLogger(ParticipantServiceImpl.class);
    private final MessageRepository msgRepo;
    private final RealtimeMessagingService rtmService;
    private final UserContextProvider userContextProvider;


    public ParticipantServiceImpl(MessageRepository msgRepo, RealtimeMessagingService rtmService, UserContextProvider userContextProvider) {
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
    public MessageEntity sendMessage(SendMessageRequest request) {
        var now = Instant.now();
        var id = msgRepo.getNextMessageId();
        var entity = new MessageEntity(id, request.convId(), request.content(), userContextProvider.getUserContext().id(), now.getEpochSecond());
        msgRepo.saveMessage(entity);
        msgRepo.updateConversationLastMessage(request.convId(), entity);

        var conversation = msgRepo.getConversation(request.convId());
        rtmService.sendMessageNotification(conversation, entity);

        logger.info("User {} send a message to {}", userContextProvider.getUserContext().id(), request.convId());
        return entity;
    }

    @Override
    public List<MessageDTO> getMessages(long convId) {
        var allMessages = msgRepo.findAllMessages(convId);
        return allMessages.stream().map(e -> new MessageDTO(e.id(), e.content(), e.senderId())).toList();
    }
}
