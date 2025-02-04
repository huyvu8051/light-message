package com.huyvu.lightmessage.controller;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.CursorPagingResponseDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.repository.MessageRepoImpl;
import com.huyvu.lightmessage.security.UserContextProvider;
import com.huyvu.lightmessage.service.MessageService;
import com.huyvu.lightmessage.service.MessageService.MessageCursor;
import com.huyvu.lightmessage.util.CursorPaging;
import com.huyvu.lightmessage.util.Paging;
import com.huyvu.lightmessage.util.PagingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class MessageControllerV1 {
    private final MessageService messageService;
    private final UserContextProvider userCtxProvider;

    public MessageControllerV1(MessageService messageService, UserContextProvider userCtxProvider) {
        this.messageService = messageService;
        this.userCtxProvider = userCtxProvider;
    }

    @GetMapping("/conversations")
    List<MessageRepoImpl.ConversationDto> conversations(OffsetDateTime cursor) {
        return messageService.getNewestConversations(userCtxProvider.getUserContext().id(), new Paging(cursor));
    }


    @PostMapping("/conversations")
    ResponseEntity<ConversationEntity> conversations(@RequestBody CreateConversationRequestDTO request) {
        var conversation = messageService.createGroupChatConversation(userCtxProvider.getUserContext().id(), request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(conversation.id())
                .toUri();

        return ResponseEntity.created(location)
                .body(conversation);
    }


    @GetMapping("/messages/{convId}")
    CursorPagingResponseDTO<MessageDTO> messages(@PathVariable long convId, @RequestParam(defaultValue = "") String nextCursor, @RequestParam(defaultValue = "5") int limit) {
        var cursor = PagingUtils.decrypt(nextCursor, MessageCursor.class);
        if(cursor == null){
            cursor = new MessageCursor(null, null);
        }
        var cp = new CursorPaging<>(limit, cursor);
        var messages = messageService.getMessages(userCtxProvider.getUserContext().id(), convId, cp);
        var encryptedCursor = PagingUtils.encrypt(messages.nextCursor());

        return CursorPagingResponseDTO.<MessageDTO>builder()
                .data(messages.data())
                .nextCursor(encryptedCursor)
                .build();
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
