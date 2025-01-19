package com.huyvu.lightmessage.dto;

import java.util.UUID;

public record SendMessageRequestDTO(long convId, String content) {
}
