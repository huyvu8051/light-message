package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.entity.MessageEntity;
import com.huyvu.lightmessage.jpa.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageJpaRepo extends JpaRepository<Message, Long> {

    @Query("""
    select m
    from Message m
    where m.conv.id = :convId""")
    List<Message> findAllByConversationId(long convId);
}
