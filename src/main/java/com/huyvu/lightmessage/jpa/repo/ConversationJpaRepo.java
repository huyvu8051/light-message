package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.jpa.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationJpaRepo extends JpaRepository<Conversation, Long> {
}
