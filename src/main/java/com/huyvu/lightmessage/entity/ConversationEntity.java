package com.huyvu.lightmessage.entity;

import lombok.Builder;

@Builder
public record ConversationEntity(long id,
                                 String name,
                                 boolean isGroupChat) {

}
