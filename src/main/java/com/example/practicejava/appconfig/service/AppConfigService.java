package com.example.practicejava.appconfig.service;

import com.example.practicejava.appconfig.AppConfigEntity;
import com.example.practicejava.appconfig.AppConfigNotFoundException;
import com.example.practicejava.appconfig.dto.UpdateAppConfigRequest;
import com.example.practicejava.appconfig.repository.AppConfigRepository;
import com.example.practicejava.common.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AppConfigService {

    private final AppConfigRepository appConfigRepository;

    public AppConfigService(AppConfigRepository appConfigRepository) {
        this.appConfigRepository = appConfigRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.CACHE_APP_CONFIG, key = "'all'")
    public List<AppConfigEntity> findAll() {
        return appConfigRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.CACHE_APP_CONFIG, key = "#key")
    public AppConfigEntity findByKey(String key) {
        return appConfigRepository.findById(key).orElseThrow(() -> new AppConfigNotFoundException(key));
    }

    @CacheEvict(value = CacheConfig.CACHE_APP_CONFIG, allEntries = true)
    public AppConfigEntity update(String key, UpdateAppConfigRequest request) {
        AppConfigEntity config = findByKey(key);
        config.setValue(request.value());
        return config;
    }
}
