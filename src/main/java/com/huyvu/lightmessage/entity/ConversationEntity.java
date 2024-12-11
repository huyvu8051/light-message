package com.huyvu.lightmessage.entity;

public record ConversationEntity(long id, String name, boolean isGroupChat, long createdAt, long lastMessageId, long lastUpdated) {
}
