package com.huyvu.lightmessage.controller;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.SendMessageResponseDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/conversations")
    List<ConversationEntity> conversations() {
        return messageService.getAllConversations();
    }


    @PostMapping("/conversations")
    ResponseEntity<ConversationEntity> conversations(@RequestBody CreateConversationRequestDTO request){
        var conversation = messageService.createGroupChatConversation(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(conversation.id())
                .toUri();

        return ResponseEntity.created(location)
                .body(conversation);
    }


    @GetMapping("/messages")
    List<MessageDTO> messages(long convId) {
        return messageService.getMessages(convId);
    }

    @PostMapping("/messages")
    ResponseEntity<SendMessageResponseDTO> messages(@RequestBody SendMessageRequestDTO msg) {
        var messageEntity = messageService.sendMessage(msg);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(messageEntity.id())
                .toUri();

        var responseBody = new SendMessageResponseDTO(messageEntity.id(), messageEntity.content(), messageEntity.senderId(), messageEntity.timestamp());
        return ResponseEntity
                .created(location)
                .body(responseBody);
    }


}
