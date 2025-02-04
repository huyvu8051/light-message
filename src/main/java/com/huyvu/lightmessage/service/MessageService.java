package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.CursorPagingResponseDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.repository.MessageRepoImpl;
import com.huyvu.lightmessage.util.CursorPaging;
import com.huyvu.lightmessage.util.Paging;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.List;

public interface MessageService {
    void sendMessage(long userId, SendMessageRequestDTO request);

    @EqualsAndHashCode(callSuper = true)
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    class MessageCursorPaging extends CursorPaging {
        OffsetDateTime sendAt;
        long id;
    }
    CursorPagingResponseDTO<MessageDTO> getMessages(long userId, long convId, MessageCursorPaging paging);

    ConversationEntity createGroupChatConversation(long userId, CreateConversationRequestDTO request);

    List<MessageRepoImpl.ConversationDto> getNewestConversations(long userId, Paging paging);
}
