package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.jpa.model.Message;
import com.huyvu.lightmessage.service.MessageService.MessageCursor;
import com.huyvu.lightmessage.util.CursorPaging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageJpaRepo extends JpaRepository<Message, Long> {

    @Query("""
            select m
            from Message m
            where m.conv.id = :convId
            AND (cast(:sendAt as TIMESTAMP) IS NULL OR m.sendAt > :sendAt)
            AND (:id IS NULL OR m.id > :id)
            order by m.sendAt desc, m.id desc
            limit :limit""")
    List<Message> findAllByConversationId(long convId, int limit, OffsetDateTime sendAt, Long id);

    Optional<Message> findOneByConvIdOrderBySendAtDesc(long id);
}
