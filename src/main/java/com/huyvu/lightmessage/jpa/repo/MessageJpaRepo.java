package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.jpa.model.Message;
import lombok.Builder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MessageJpaRepo extends JpaRepository<Message, Long> {

    @Builder
    record SendMessage(
            long id,
            long convId,
            String content,
            long senderId,
            long sendAt
    ) {
    }

    @Modifying
    @Query("""
        insert into message
        (convId, content, senderId, sendAt)
        values (convId, content, senderId, sendAt)
        """)
    void saveNew(SendMessage entity);
}
