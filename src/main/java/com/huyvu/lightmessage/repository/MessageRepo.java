package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.jpa.model.Member;
import com.huyvu.lightmessage.service.MessageService;
import com.huyvu.lightmessage.util.CursorPaging;
import com.huyvu.lightmessage.util.CursorPagingResult;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepo {

    //    @Cacheable(value = "findAllMessages")
    CursorPagingResult<MessageDTO, MessageService.MessageCursor> findAllMessages(long convId, CursorPaging<MessageService.MessageCursor> paging);

    long saveMessage(MessageEntity message);

    void saveConversation(ConversationEntity conversation);

    List<MessageRepoImpl.ConversationDto> findAllConversations(long userId, CursorPaging<MessageService.ConversationCursor > paging);

    Optional<Member> findMember(long userId, long convId);

    void updateMemberLastSendAt(long convId, OffsetDateTime offsetDateTime);

    Optional<ConversationEntity> findConversation(long convId);

    Optional<MessageEntity> findConversationLastMessage(long convId);

    List<Long> findAllMembers(long convId);
}
