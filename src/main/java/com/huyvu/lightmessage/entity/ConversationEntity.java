package com.huyvu.lightmessage.entity;

import java.time.Instant;

public record ConversationEntity(long id, String name, boolean isGroupChat, long createdAt, long lastMessageId, long lastUpdated) {

}
