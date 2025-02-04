package com.huyvu.lightmessage.controller;

import com.huyvu.lightmessage.dto.CursorPagingResponseDTO;
import com.huyvu.lightmessage.security.UserContext;
import com.huyvu.lightmessage.security.UserContextProvider;
import com.huyvu.lightmessage.service.MessageService;
import com.huyvu.lightmessage.util.CursorPagingResult;
import com.huyvu.lightmessage.util.PagingUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = MessageControllerV1.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class MessageControllerV1Test {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UserContextProvider userContextProvider;

    String cursor;

    @BeforeEach
    void setUp() {
        MessageService.MessageCursor mc = new MessageService.MessageCursor(OffsetDateTime.now(), 69L);
        this.cursor = PagingUtils.encrypt(mc);

    }

    @Test
    void getMessagesWithoutRequestParam() throws Exception {
        try (MockedStatic<PagingUtils> mockedStatic = Mockito.mockStatic(PagingUtils.class)) {
//            mockedStatic.when(() -> PagingUtils.decrypt(any(), any())).thenReturn(new MessageService.MessageCursor());

            when(userContextProvider.getUserContext()).thenReturn(new UserContext(1));
            when(messageService.getMessages(anyLong(), anyLong(), any())).thenReturn(new CursorPagingResult<>(List.of(), null));
            mockMvc.perform(get("/api/v1/messages/2"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void getMessagesWithRequestParamLimit() throws Exception {
        try (MockedStatic<PagingUtils> mockedStatic = Mockito.mockStatic(PagingUtils.class)) {
            ArgumentCaptor<String> pageDtoCaptor = ArgumentCaptor.forClass(String.class);

            mockedStatic.when(() -> PagingUtils.decrypt(pageDtoCaptor.capture(), any())).thenCallRealMethod();

            when(userContextProvider.getUserContext()).thenReturn(new UserContext(1));
            when(messageService.getMessages(anyLong(), anyLong(), any()))
                    .thenReturn(new CursorPagingResult<>(List.of(), null));


            mockMvc.perform(get("/api/v1/messages/2?limit=2"))
                    .andExpect(status().isOk());

            String capturedPageDto = pageDtoCaptor.getValue();
            assertNotNull(capturedPageDto);
            assertTrue(capturedPageDto.isBlank());
        }
    }


    @Test
    void getMessagesWithRequestParamLimitAndCursor() throws Exception {
        try (MockedStatic<PagingUtils> mockedStatic = Mockito.mockStatic(PagingUtils.class)) {
            ArgumentCaptor<String> pageDtoCaptor = ArgumentCaptor.forClass(String.class);

            mockedStatic.when(() -> PagingUtils.decrypt(pageDtoCaptor.capture(), any())).thenCallRealMethod();

            when(userContextProvider.getUserContext()).thenReturn(new UserContext(1));
            when(messageService.getMessages(anyLong(), anyLong(), any()))
                    .thenReturn(new CursorPagingResult<>(List.of(), null));


            mockMvc.perform(get("/api/v1/messages/2?limit=2&nextCursor=" + cursor))
                    .andExpect(status().isOk());

            String capturedPageDto = pageDtoCaptor.getValue();
            assertNotNull(capturedPageDto);
            assertEquals(cursor, capturedPageDto);
        }
    }


}