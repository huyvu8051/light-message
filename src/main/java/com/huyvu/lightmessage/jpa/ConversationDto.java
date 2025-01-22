package com.huyvu.lightmessage.jpa;

import com.huyvu.lightmessage.jpa.repo.ConversationJpaRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record ConversationDto (
    long id,
    String name,
    boolean isGroupChat,
    ConversationJpaRepo.MessageDto message
){
    public ConversationDto(long id,
                           String name,
                           boolean isGroupChat,
                           long messageId,
                           String messageContent,
                           long sendAt){
        this(id, name, isGroupChat, new ConversationJpaRepo.MessageDto(messageId, messageContent, sendAt));
    }
}