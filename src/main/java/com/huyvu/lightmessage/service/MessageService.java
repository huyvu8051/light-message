package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.repository.MessageRepoImpl;
import com.huyvu.lightmessage.util.CursorPaging;
import com.huyvu.lightmessage.util.CursorPagingResult;

import java.time.OffsetDateTime;

public interface MessageService {
    void sendMessage(long userId, SendMessageRequestDTO request);

    MessageRepoImpl.ConversationDto getNewestConversation(long id, long convId);

    CursorPagingResult<MessageRepoImpl.ConversationDto, ConversationCursor> getNewestConversation(long userId, CursorPaging<ConversationCursor> paging);

    record MessageCursor(OffsetDateTime sendAt,
                         Long id) {
    }

    CursorPagingResult<MessageDTO, MessageCursor> getMessages(long userId, long convId, CursorPaging<MessageCursor> paging);

    ConversationEntity createGroupChatConversation(long userId, CreateConversationRequestDTO request);




    record ConversationCursor(int limit) {
    }
}
