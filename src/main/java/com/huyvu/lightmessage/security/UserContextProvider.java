package com.huyvu.lightmessage.security;

import reactor.core.publisher.Mono;

public interface UserContextProvider {

    @Deprecated
    UserContext getUserContext();
    Mono<UserContext> getUserContextR2();
}
