package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.repository.MessageRepoImpl;
import com.huyvu.lightmessage.util.CursorPaging;
import com.huyvu.lightmessage.util.CursorPagingResult;
import com.huyvu.lightmessage.util.Paging;

import java.time.OffsetDateTime;
import java.util.List;

public interface MessageService {
    void sendMessage(long userId, SendMessageRequestDTO request);

    record MessageCursor(OffsetDateTime sendAt,
                         Long id) {
    }

    CursorPagingResult<MessageDTO, MessageCursor> getMessages(long userId, long convId, CursorPaging<MessageCursor> paging);

    ConversationEntity createGroupChatConversation(long userId, CreateConversationRequestDTO request);

    List<MessageRepoImpl.ConversationDto> getNewestConversations(long userId, Paging paging);
}
