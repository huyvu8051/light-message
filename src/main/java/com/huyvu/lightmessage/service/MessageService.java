package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;

import java.util.List;

public interface MessageService {
    MessageEntity sendMessage(SendMessageRequestDTO request);
    List<MessageDTO> getMessages(long convId);

    ConversationEntity createGroupChatConversation(CreateConversationRequestDTO request);
}
