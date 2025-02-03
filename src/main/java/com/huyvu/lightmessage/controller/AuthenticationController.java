package com.huyvu.lightmessage.controller;

import com.huyvu.lightmessage.service.AuthenticationService;
import lombok.*;
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

    static class AuthReqDTO extends AuthenticationService.Auth {
    }

    @Builder
    record AuthRespDTO(int userId){
    }

    @PostMapping("auth")
    ResponseEntity<AuthRespDTO> auth(@RequestBody AuthReqDTO authReqDTO){
        authenticationService.authenticate(authReqDTO);
        String jwtToken = String.valueOf(authReqDTO.getUserId());
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
                .body(AuthRespDTO.builder()
                        .userId(authReqDTO.getUserId())
                        .build());

    }
}
