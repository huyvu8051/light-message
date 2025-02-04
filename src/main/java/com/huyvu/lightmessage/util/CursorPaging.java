package com.huyvu.lightmessage.util;

public record CursorPaging<C>(
        int limit,
        C cursor
) {
}