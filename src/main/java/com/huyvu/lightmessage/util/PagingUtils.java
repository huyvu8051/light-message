package com.huyvu.lightmessage.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.huyvu.lightmessage.exception.CursorPagingException;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;

@UtilityClass
public class PagingUtils {
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public <T> String encrypt(T object) {
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new CursorPagingException("encrypt false");
        }
        return Base64.getUrlEncoder().encodeToString(jsonString.getBytes());
    }

    @Nullable
    public  <T> T decrypt(String encrypted, Class<T> des) {
        if(encrypted == null || encrypted.isEmpty()) {
            return null;
        }

        var decoded = Base64.getUrlDecoder().decode(encrypted);
        try {
            return objectMapper.readValue(decoded, des);
        } catch (IOException e) {
            throw new CursorPagingException("decrypt false");
        }
    }

}