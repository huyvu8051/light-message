package com.huyvu.lightmessage.controller;

import com.huyvu.lightmessage.controller.dto.SendMessageResponseDTO;
import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequest;
import com.huyvu.lightmessage.service.ParticipantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/participant")
public class ParticipantController {
    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }


    @GetMapping("/messages")
    List<MessageDTO> message(long convId) {
        return participantService.getMessages(convId);
    }

    @PostMapping("/messages")
    ResponseEntity<SendMessageResponseDTO> message(@RequestBody SendMessageRequest msg) {
        var messageEntity = participantService.sendMessage(msg);

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
