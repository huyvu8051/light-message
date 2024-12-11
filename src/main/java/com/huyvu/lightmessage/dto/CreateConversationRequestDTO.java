package com.huyvu.lightmessage.dto;

import java.util.List;

public record CreateConversationRequestDTO(String conversationName, List<Long> participants) {
}
