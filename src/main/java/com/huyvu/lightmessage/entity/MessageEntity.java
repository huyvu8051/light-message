package com.huyvu.lightmessage.entity;

import lombok.*;

import java.util.UUID;

@Builder
public record MessageEntity(
        UUID id,
        UUID convId,
        String content,
        UUID senderId,
        long sentAt
) {
}
