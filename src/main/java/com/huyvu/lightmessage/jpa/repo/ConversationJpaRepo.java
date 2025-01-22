package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.jpa.model.Conversation;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;
public interface ConversationJpaRepo extends JpaRepository<Conversation, UUID> {

    record MessageDto(long messageId,
                      String messageContent,
                      long sendAt) {
    }

    record ConversationDto(
            long id,
            String name,
            boolean isGroupChat,
            MessageDto message
    ) {
        public ConversationDto(long id,
                               String name,
                               boolean isGroupChat,
                               long messageId,
                               String messageContent,
                               long sendAt) {
            this(id, name, isGroupChat, new MessageDto(messageId, messageContent, sendAt));
        }
    }

    /*@Query("""
            select new com.huyvu.lightmessage.jpa.repo.ConversationJpaRepo$ConversationDto
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
            """)*/
    /*@Query("""
            select new com.huyvu.lightmessage.jpa.repo.ConversationJpaRepo$ConversationDto(0, "", false, 0, "", 0)
                from Member mem
                left join fetch Message mes
                on mem.conversation.id = mes.conv.id
                where mem.user.id = :userId
                order by mes.sendAt desc
           
            """)
    List<ConversationDto> findAllByMemberId(long userId);*/


    @Query(value = """
            SELECT conv.id AS conv_id, conv.name, conv.is_group_chat, m.id AS m_id, m.content, m.send_at
              FROM (SELECT conversation_id
                      FROM member
                     WHERE user_id = :userId) AS conv_ids(conv_id)
                       LEFT JOIN LATERAL (
                  SELECT *
                    FROM message
                   WHERE conv_id = conv_ids.conv_id
                   ORDER BY send_at DESC
                   LIMIT 1
                  ) m ON TRUE
                       LEFT JOIN conversation conv
                                 ON conv.id = m.conv_id
            """,
            nativeQuery = true)
    List<Tuple> findAllByMemberId(long userId);


}
