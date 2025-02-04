package com.huyvu.lightmessage.util;

import lombok.Builder;

import java.util.List;

@Builder
public record CursorPagingResult<T, C>(
        List<T> data,
        C nextCursor
) {
}