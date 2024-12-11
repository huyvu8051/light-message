package com.huyvu.lightmessage.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

@Configuration
public class TokenBasedSecurityFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authorization = request.getHeader("Authorization");
        if(Strings.isNotBlank(authorization)){
            var context = SecurityContextHolder.getContext();
            var authentication = context.getAuthentication();
            if(authentication instanceof AnonymousAuthenticationToken){
                var userId = Long.parseLong(authorization);
                var userContext = new UserContext(userId);
                var preAuth = new PreAuthenticatedAuthenticationToken(userContext, authorization);
                context.setAuthentication(preAuth);
            }
        }

        filterChain.doFilter(request,response);
    }
}
