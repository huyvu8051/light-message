package com.huyvu.lightmessage.entity;

import lombok.*;

@Builder
public record MessageEntity(
        long id,
        long convId,
        String content,
        long senderId,
        long timestamp
) {
}
