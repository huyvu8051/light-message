package com.huyvu.lightmessage.controller;

import com.huyvu.lightmessage.dto.CursorPagingResponseDTO;
import com.huyvu.lightmessage.security.UserContext;
import com.huyvu.lightmessage.security.UserContextProvider;
import com.huyvu.lightmessage.service.MessageService;
import com.huyvu.lightmessage.util.PagingUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerV1Test {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UserContextProvider userContextProvider;


    @BeforeEach
    void setUp() {

    }

    @Test
    void conversations() {

    }

    @Test
    void messagesAndSuccessParseRequestParamIntoDto() throws Exception {

        try (MockedStatic<PagingUtils> mockedStatic = Mockito.mockStatic(PagingUtils.class)) {
            mockedStatic.when(() -> PagingUtils.decrypt(any(), any())).thenReturn(new MessageService.MessageCursorPaging());

            when(userContextProvider.getUserContext()).thenReturn(new UserContext(1));
            when(messageService.getMessages(anyLong(), anyLong(), any())).thenReturn(new CursorPagingResponseDTO<>(List.of(), null));

            mockMvc.perform(get("/api/v1/messages/2"))
                    .andExpect(status().isOk());
        }


    }
}