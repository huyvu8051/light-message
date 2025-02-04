package com.huyvu.lightmessage.jpa.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CursorPagingRequestDTO {
    String nextCursor = "";
    int limit = 5;
}
