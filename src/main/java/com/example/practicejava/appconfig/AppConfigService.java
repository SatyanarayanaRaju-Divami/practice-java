package com.example.practicejava.appconfig;

import com.example.practicejava.appconfig.dto.UpdateAppConfigRequest;
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
    public List<AppConfigEntity> findAll() {
        return appConfigRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AppConfigEntity findByKey(String key) {
        return appConfigRepository.findById(key).orElseThrow(() -> new AppConfigNotFoundException(key));
    }

    public AppConfigEntity update(String key, UpdateAppConfigRequest request) {
        AppConfigEntity config = findByKey(key);
        config.setValue(request.value());
        return config;
    }
}
