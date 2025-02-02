package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.exception.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public void authenticate(Auth auth) throws AuthenticationException {
        if(auth.getUserId() < 0){
            throw new AuthenticationException("Invalid user id: " + auth.getUserId());
        }
    }
}
