package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.jpa.model.Conversation;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConversationJpaRepo extends JpaRepository<Conversation, Long> {
    @Query(value = """
                   SELECT conv_ids.conv_id AS conv_id
                        , conv.name
                        , conv.is_group_chat
                        , m.id             AS m_id
                        , m.content
                        , m.send_at
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
                                        ON conv.id = conv_ids.conv_id
                   """, nativeQuery = true)
    List<Tuple> findNewestConversation(long userId);


    @Query("""
           select c
           from Member m
           left join Conversation c on m.conversation.id = c.id
           where m.user.id = :userId""")
    List<Conversation> findConversationsByUserId(long userId);
}
