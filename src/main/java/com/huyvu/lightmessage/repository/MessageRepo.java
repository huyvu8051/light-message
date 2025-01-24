package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.jpa.model.Member;
import com.huyvu.lightmessage.util.Paging;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepo {
    List<MessageEntity> findAllMessages(long convId, OffsetDateTime from, OffsetDateTime to);

    void saveMessage(MessageEntity message);

    long getNextConversationId();

    void saveConversation(ConversationEntity conversation);

    List<MessageRepoImpl.ConversationDto> findAllConversations(long userId, Paging paging);

    Optional<Member> findMember(long userId, long convId);
}
