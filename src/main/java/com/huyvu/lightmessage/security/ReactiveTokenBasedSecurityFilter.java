package com.huyvu.lightmessage.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNullApi;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class ReactiveTokenBasedSecurityFilter implements WebFilter {
    private final ServerSecurityContextRepository securityContextRepository =  NoOpServerSecurityContextRepository.getInstance();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        var cookies = request.getCookies();
        var authorization = cookies.getFirst("Authorization");

        return Mono.justOrEmpty(authorization)
                .map(HttpCookie::getValue)
                .filter(Strings::isNotBlank)
                .map(token -> {
                    Long userId = Long.parseLong(token);
                    var userContext = new UserContext(userId);
                    var authentication = new PreAuthenticatedAuthenticationToken(userContext, token, List.of());
                    return new SecurityContextImpl(authentication);
                })
                .flatMap(securityContext -> securityContextRepository.save(exchange, securityContext)
                        .thenReturn(securityContext))
                .switchIfEmpty(Mono.defer(() -> chain.filter(exchange).then(Mono.empty())))
                .flatMap(securityContext -> chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))));
    }
}
