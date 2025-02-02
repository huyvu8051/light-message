package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.exception.AuthenticationException;
import lombok.Data;

public interface AuthenticationService {
    @Data
    class Auth {
        int userId;
    }

    void authenticate(Auth auth) throws AuthenticationException;
}
