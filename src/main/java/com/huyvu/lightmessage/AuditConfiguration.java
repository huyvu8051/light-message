package com.huyvu.lightmessage;

import com.huyvu.lightmessage.exception.AuthenticationException;
import com.huyvu.lightmessage.security.UserContextProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditConfiguration {
    @Bean
    public AuditorAware<String> auditorProvider(UserContextProvider userContextProvider) {
        return () -> {
            try {
                var userContext = userContextProvider.getUserContext();
                return String.valueOf(userContext.id()).describeConstable();
            } catch (AuthenticationException e) {
                return "Anonymous".describeConstable();
            }
        };
    }
}