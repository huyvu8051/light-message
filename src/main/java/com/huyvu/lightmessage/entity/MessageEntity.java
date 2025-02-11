package com.huyvu.lightmessage.entity;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record MessageEntity(
        long id,
        long convId,
        String content,
        long senderId,
        OffsetDateTime sentAt,
        List<Long> memberIds

) {
}
