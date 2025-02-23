package com.huyvu.lightmessage.r2;

import com.huyvu.lightmessage.r2.model.Member;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface R2MemberRepo extends R2dbcRepository<Member, Long> {


    Mono<Member> findByUserIdAndConversationId(long userId, long convId);
}
