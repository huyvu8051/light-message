package com.huyvu.lightmessage.controller.dto;

public record SendMessageResponseDTO(long id, String content, long senderId, long timestamp) {
}
