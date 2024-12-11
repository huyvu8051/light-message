package com.huyvu.lightmessage.security;

import com.huyvu.lightmessage.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class UserContextProviderImpl implements UserContextProvider {
    Logger logger = LoggerFactory.getLogger(UserContextProviderImpl.class);

    @Override
    public UserContext getUserContext() {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        var principal = authentication.getPrincipal();
        if (principal instanceof UserContext userContext) {
            return userContext;
        }
        throw new AuthenticationException("Not authorized");
    }
}
