package com.huyvu.lightmessage.entity;

import lombok.*;

import java.util.UUID;

@Builder
public record MessageEntity(
        long id,
        long convId,
        String content,
        long senderId,
        long sentAt
) {
}
