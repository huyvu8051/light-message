package com.huyvu.lightmessage.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huyvu.lightmessage.exception.CursorPagingException;
import com.huyvu.lightmessage.jpa.dto.CursorPagingRequestDTO;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Base64;

@UtilityClass
public class PagingUtils {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> String encrypt(T object) {
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new CursorPagingException("encrypt false");
        }
        return Base64.getUrlEncoder().encodeToString(jsonString.getBytes());
    }

    public  <T extends CursorPaging> T decrypt(CursorPagingRequestDTO dto, Class<T> des) {
        var decode = Base64.getUrlDecoder().decode(dto.getNextCursor());
        try {
            var cp = objectMapper.readValue(decode, des);
            cp.setLimit(dto.getLimit());
            return cp;
        } catch (IOException e) {
            throw new CursorPagingException("decrypt false");
        }
    }
}