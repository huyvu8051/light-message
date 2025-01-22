package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.jpa.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageJpaRepo extends JpaRepository<Message, Long> {

/*
    EXPLAIN ANALYZE
    SELECT DISTINCT ON (conv_id) *
    FROM message
    WHERE conv_id in (1, 2)
    ORDER BY conv_id, send_at DESC;
    */
    @Query("""
    select new com.huyvu.lightmessage.entity.MessageEntity(id, conv.id, content, sender.id, sendAt )
    from Message
    where conv.id = :convId""")
    List<MessageEntity> findAllByConversationId(long convId);
}
