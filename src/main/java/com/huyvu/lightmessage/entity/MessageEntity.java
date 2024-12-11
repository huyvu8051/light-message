package com.huyvu.lightmessage.entity;

public record MessageEntity(long id, long convId,  String content, long senderId, long timestamp) {
}
