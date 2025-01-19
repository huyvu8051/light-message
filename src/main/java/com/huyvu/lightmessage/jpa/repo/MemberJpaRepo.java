package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.jpa.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface MemberJpaRepo extends JpaRepository<Member, Long> {
    Optional<Member> findOneByUserIdAndConversationId(long userId, long convId);
}
