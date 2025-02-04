package com.huyvu.lightmessage.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record MessageDTO(long id, String content, long senderId, OffsetDateTime sendAt) {
}
