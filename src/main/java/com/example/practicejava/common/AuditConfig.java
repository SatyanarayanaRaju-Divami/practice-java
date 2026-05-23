package com.example.practicejava.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    // Placeholder — will be replaced with JWT principal extraction once auth is implemented
    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return Optional::empty;
    }
}
