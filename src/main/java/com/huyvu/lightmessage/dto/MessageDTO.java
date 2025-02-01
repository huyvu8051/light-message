package com.huyvu.lightmessage.dto;

import java.time.OffsetDateTime;

public record MessageDTO(long id, String content, long senderId, OffsetDateTime sendAt) {
}
