package com.huyvu.lightmessage;

import com.huyvu.lightmessage.dto.CreateConversationRequestDTO;
import com.huyvu.lightmessage.dto.SendMessageRequestDTO;
import com.huyvu.lightmessage.dto.SendMessageResponseDTO;
import com.huyvu.lightmessage.entity.ConversationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LightMessageApplicationTests {
    static final long CURR_USER_ID = 1100001;
    static final long SEC_USER_ID = 1100002;
    static final long THIRD_USER_ID = 1100003;

//    @Autowired
    private TestRestTemplate testRestTemplate;

    private long createdConversationId;

    @BeforeEach
    void setup(){
        var request = new CreateConversationRequestDTO("Duck my sick", List.of(CURR_USER_ID, SEC_USER_ID, THIRD_USER_ID));

        // Create headers with Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.valueOf(CURR_USER_ID));

        // Wrap the request and headers in an HttpEntity
        HttpEntity<CreateConversationRequestDTO> httpEntity = new HttpEntity<>(request, headers);

        // Act: Make a POST request to the /messages endpoint
        ResponseEntity<ConversationEntity> response = testRestTemplate.exchange(
                "/api/v1/conversations",
                HttpMethod.POST,
                httpEntity,
                ConversationEntity.class
        );

        this.createdConversationId = response.getBody().id();
    }


//    @Test
    void shouldCreateMessageSuccessfully() {


        // Arrange: Create a request body for the message
        SendMessageRequestDTO request = new SendMessageRequestDTO(createdConversationId, "Hello, this is a test message!");

        // Create headers with Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "1100001");

        // Wrap the request and headers in an HttpEntity
        HttpEntity<SendMessageRequestDTO> httpEntity = new HttpEntity<>(request, headers);

        // Act: Make a POST request to the /messages endpoint
        ResponseEntity<SendMessageResponseDTO> response = testRestTemplate.exchange(
                "/api/v1/messages",
                HttpMethod.POST,
                httpEntity,
                SendMessageResponseDTO.class
        );

        // Assert: Validate the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotZero();
        assertThat(response.getBody().content()).isEqualTo("Hello, this is a test message!");
        assertThat(response.getBody().senderId()).isNotZero();
        assertThat(response.getBody().timestamp()).isNotZero();
    }



}
