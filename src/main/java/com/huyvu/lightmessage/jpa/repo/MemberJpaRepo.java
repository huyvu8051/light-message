package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.jpa.model.Conversation;
import com.huyvu.lightmessage.jpa.model.Member;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;


public interface MemberJpaRepo extends JpaRepository<Member, Long> {
    Optional<Member> findOneByConversationIdAndUserId(long convId, long userId);
    @Modifying
    @Transactional
    @Query("""
            update Member
               set lastSendAt = :time
             where id = :convId""")
    void updateById(long convId, OffsetDateTime time);

}
