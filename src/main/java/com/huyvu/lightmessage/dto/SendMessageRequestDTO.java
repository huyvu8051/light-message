package com.huyvu.lightmessage.dto;

import java.util.UUID;

public record SendMessageRequestDTO(UUID convId, String content) {
}
