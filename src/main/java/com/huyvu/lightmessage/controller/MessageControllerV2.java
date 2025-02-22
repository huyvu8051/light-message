package com.huyvu.lightmessage.controller;

import com.huyvu.lightmessage.dto.CursorPagingResponseDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.security.UserContextProvider;
import com.huyvu.lightmessage.service.MessageService;
import com.huyvu.lightmessage.service.MessageService.MessageCursor;
import com.huyvu.lightmessage.util.CursorPaging;
import com.huyvu.lightmessage.util.PagingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v2")
public class MessageControllerV2 {
    private final MessageService messageService;
    private final UserContextProvider userCtxProvider;

    public MessageControllerV2(MessageService messageService, UserContextProvider userCtxProvider) {
        this.messageService = messageService;
        this.userCtxProvider = userCtxProvider;
    }


    @GetMapping("/messages/{convId}")
    Mono<CursorPagingResponseDTO<MessageDTO>> messages(@PathVariable long convId, @RequestParam(defaultValue = "") String nextCursor, @RequestParam(defaultValue = "5") int limit) {
        var cursor = PagingUtils.decrypt(nextCursor, MessageCursor.class);
        if (cursor == null) {
            cursor = new MessageCursor(null, null);
        }
        var cp = new CursorPaging<>(limit, cursor);


        return messageService.getMessagesR2(userCtxProvider.getUserContext().id(), convId, cp)
                .map(cpr -> {
                    var encrypt = PagingUtils.encrypt(cpr.nextCursor());
                    return CursorPagingResponseDTO.<MessageDTO>builder()
                            .data(cpr.data())
                            .nextCursor(encrypt)
                            .build();
                });
    }

    @PostMapping("/messages")
    ResponseEntity<Void> messages(@RequestBody SendMessageRequestDTO msg) {
        messageService.sendMessage(userCtxProvider.getUserContext().id(), msg);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(msg.convId())
                .toUri();

        return ResponseEntity
                .created(location)
                .build();
    }


}
