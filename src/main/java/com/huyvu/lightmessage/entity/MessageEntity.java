package com.huyvu.lightmessage.entity;

import lombok.Builder;

@Builder
public record MessageEntity(long id, long convId,  String content, long senderId, long timestamp) {
}
