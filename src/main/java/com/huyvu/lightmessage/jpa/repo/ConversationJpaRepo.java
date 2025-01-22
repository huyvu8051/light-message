package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.jpa.ConversationDto;
import com.huyvu.lightmessage.jpa.model.Conversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public interface ConversationJpaRepo extends JpaRepository<Conversation, UUID> {

    record MessageDto(long messageId,
                      String messageContent,
                      long sendAt) {
    }



    @Query("""
            select new com.huyvu.lightmessage.jpa.ConversationDto
                        (c.id
                       , c.name
                       , c.isGroupChat
                       , m.id
                       , m.content
                       , m.sendAt)
              from Conversation c,
                          Message m
             where m.conv.id = c.id
               and c.id in (select m.id
                              from Member m
                             where m.user.id = :userId)
            """)
    List<ConversationDto> findAllByMemberId(long userId);
}
