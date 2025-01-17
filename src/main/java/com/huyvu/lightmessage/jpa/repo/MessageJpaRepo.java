package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.jpa.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageJpaRepo extends JpaRepository<Message, Long> {

}
