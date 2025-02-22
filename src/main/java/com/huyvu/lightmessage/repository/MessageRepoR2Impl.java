package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.jpa.model.Member;
import com.huyvu.lightmessage.r2.R2MemberRepo;
import com.huyvu.lightmessage.service.MessageService;
import com.huyvu.lightmessage.util.CursorPaging;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public class MessageRepoR2Impl implements MessageRepoR2 {
    private final R2MemberRepo r2MemberRepo;

    public MessageRepoR2Impl(R2MemberRepo r2MemberRepo) {
        this.r2MemberRepo = r2MemberRepo;
    }

    @Override
    public Mono<Optional<Member>> findMember(long userId, long convId) {
        return r2MemberRepo.findByUserIdAndConversationId(userId, convId).map(member -> Optional.of(new Member()));
    }

    @Scheduled(fixedRate = 5000)
    public void scheduled() {
        var byUserIdAndConversationId = r2MemberRepo.findByUserIdAndConversationId(2, 3);
        byUserIdAndConversationId.subscribe(member -> System.out.println(member.toString()));
    }

    @Override
    public Flux<MessageDTO> findAllMessages(long convId, CursorPaging<MessageService.MessageCursor> paging) {
        return Flux.empty();
    }
}
