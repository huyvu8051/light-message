package com.huyvu.lightmessage.repository;

import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.jpa.model.Member;
import com.huyvu.lightmessage.service.MessageService;
import com.huyvu.lightmessage.util.CursorPaging;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.stream.Collectors;

public interface MessageRepoR2 {

    Mono<Optional<Member>> findMember(long userId, long convId);

    Flux<MessageDTO> findAllMessages(long convId, CursorPaging<MessageService.MessageCursor> paging);
}
