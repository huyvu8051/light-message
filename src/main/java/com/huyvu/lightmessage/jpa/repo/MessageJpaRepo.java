package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.jpa.model.Message;
import com.huyvu.lightmessage.service.MessageService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageJpaRepo extends JpaRepository<Message, Long> {

    @Query("""
            select m
            from Message m
            where m.conv.id = :convId
            and m.sendAt >= :#{#paging.sendAt}
            and m.id >= :#{#paging.id}
            order by m.sendAt desc, m.id desc
            limit 10""")
    List<Message> findAllByConversationId(long convId, MessageService.MessageCursorPaging paging);

    Optional<Message> findOneByConvIdOrderBySendAtDesc(long id);
}
