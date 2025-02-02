package com.huyvu.lightmessage.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Configuration
public class TokenBasedSecurityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var cookies = request.getCookies();
        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        var authorizationOpts = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("Authorization")).findFirst();
        if(authorizationOpts.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }

        var authorization = authorizationOpts.get().getValue();

        if(Strings.isBlank(authorization)){
            filterChain.doFilter(request, response);
            return;
        }

        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if(authentication instanceof AnonymousAuthenticationToken){
            var userId = Long.parseLong(authorization);
            var userContext = new UserContext(userId);
            var preAuth = new PreAuthenticatedAuthenticationToken(userContext, authorization);
            context.setAuthentication(preAuth);
        }

        filterChain.doFilter(request,response);
    }
}
