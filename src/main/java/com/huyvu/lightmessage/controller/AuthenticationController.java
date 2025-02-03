package com.huyvu.lightmessage.controller;

import com.huyvu.lightmessage.service.AuthenticationService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    AuthenticationController(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }


    static class AuthDTO extends AuthenticationService.Auth {
    }

    @Builder
    record AuthResponse(int userId){
    }

    @PostMapping("auth")
    ResponseEntity<AuthResponse> auth(@RequestBody AuthDTO authDTO){
        authenticationService.authenticate(authDTO);
        String jwtToken = String.valueOf(authDTO.getUserId());
        ResponseCookie cookie = ResponseCookie.from("Authorization", jwtToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(AuthResponse.builder()
                        .userId(authDTO.getUserId())
                        .build());

    }
}
