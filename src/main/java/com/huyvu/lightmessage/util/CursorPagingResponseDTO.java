package com.huyvu.lightmessage.util;

import lombok.Builder;

import java.util.List;

@Builder
public record CursorPagingResponseDTO<T>(
        List<T> data,
        String nextCursor
) {
}