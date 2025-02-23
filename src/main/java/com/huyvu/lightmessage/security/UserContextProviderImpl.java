package com.huyvu.lightmessage.security;

import com.huyvu.lightmessage.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class UserContextProviderImpl implements UserContextProvider {
    Logger logger = LoggerFactory.getLogger(UserContextProviderImpl.class);

    @Deprecated
    @Override
    public UserContext getUserContext() {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if (authentication != null &&  authentication.getPrincipal() instanceof UserContext userContext) {
            return userContext;
        }
        throw new AuthenticationException("Not authorized");
    }

    @Override
    public Mono<UserContext> getUserContextR2() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    var authentication = securityContext.getAuthentication();
                    if (authentication != null &&  authentication.getPrincipal() instanceof UserContext userContext) {
                        return Mono.just(userContext);
                    }
                    return Mono.error(new AuthenticationException("Not authorized"));
                });
    }
}
