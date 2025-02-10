package com.huyvu.lightmessage.controller;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.CursorPagingResponseDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.repository.MessageRepoImpl;
import com.huyvu.lightmessage.security.UserContextProvider;
import com.huyvu.lightmessage.service.MessageService;
import com.huyvu.lightmessage.util.CursorPaging;
import com.huyvu.lightmessage.util.PagingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ConversationControllerV1 {
    private final MessageService messageService;
    private final UserContextProvider userCtxProvider;

    public ConversationControllerV1(MessageService messageService, UserContextProvider userCtxProvider) {
        this.messageService = messageService;
        this.userCtxProvider = userCtxProvider;
    }

    @GetMapping("/conversations/{convId}")
    MessageRepoImpl.ConversationDto conversations(@PathVariable long convId) {
        return messageService.getNewestConversation(userCtxProvider.getUserContext().id(), convId);
    }

    @GetMapping("/conversations")
    CursorPagingResponseDTO<MessageRepoImpl.ConversationDto> conversations(@RequestParam(defaultValue = "") String nextCursor, @RequestParam(defaultValue = "10") int limit) {
        var cursor = PagingUtils.decrypt(nextCursor, MessageService.ConversationCursor.class);
        if(cursor == null){
            cursor = new MessageService.ConversationCursor(limit);
        }
        var cp = new CursorPaging<>(limit, cursor);

        var newestConversations = messageService.getNewestConversation(userCtxProvider.getUserContext().id(), cp);
        var encryptedCursor = PagingUtils.encrypt(newestConversations.nextCursor());
        return CursorPagingResponseDTO.<MessageRepoImpl.ConversationDto>builder()
                .data(newestConversations.data())
                .nextCursor(encryptedCursor)
                .build();
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


}
