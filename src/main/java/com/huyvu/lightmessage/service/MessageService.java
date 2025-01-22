package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.jpa.ConversationDto;
import com.huyvu.lightmessage.jpa.repo.ConversationJpaRepo;
import com.huyvu.lightmessage.util.Paging;

import java.util.List;

public interface MessageService {
    void sendMessage(long userId, SendMessageRequestDTO request);

    List<MessageDTO> getMessages(long userId, long convId, Paging paging);

    ConversationEntity createGroupChatConversation(long userId, CreateConversationRequestDTO request);

    List<ConversationDto> getNewestConversations(long userId, Paging paging);
}
