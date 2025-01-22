package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.jpa.ConversationDto;
import com.huyvu.lightmessage.jpa.model.Member;
import com.huyvu.lightmessage.jpa.repo.ConversationJpaRepo;
import com.huyvu.lightmessage.util.Paging;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepo {
    List<MessageEntity> findAllMessages(long convId);

    void saveMessage(MessageEntity message);

    long getNextConversationId();

    void saveConversation(ConversationEntity conversation);

    List<ConversationDto> findAllConversations(long userId, Paging paging);

    Optional<Member> findMember(long userId, long convId);
}
