package com.example.practicejava.common;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CACHE_APP_CONFIG = "app-config";
    public static final String CACHE_LEAGUES    = "leagues";
    public static final String CACHE_TEAMS      = "teams";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                CACHE_APP_CONFIG, CACHE_LEAGUES, CACHE_TEAMS);
        // Different TTLs per cache would need separate managers; one spec covers all here.
        // app-config is the hottest path (called on every scheduled tick).
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(500));
        return manager;
    }
}
