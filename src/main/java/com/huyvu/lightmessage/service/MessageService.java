package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.util.Paging;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void sendMessage(UUID userId, SendMessageRequestDTO request);

    List<MessageDTO> getMessages(UUID userId, UUID convId, Paging paging);

    ConversationEntity createGroupChatConversation(UUID userId, CreateConversationRequestDTO request);

    List<ConversationEntity> getNewestConversations(UUID userId, Paging paging);
}
