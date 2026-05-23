package com.example.practicejava.appconfig.dto;

import com.example.practicejava.appconfig.AppConfigEntity;

public record AppConfigResponse(String key, String value, String description) {
    public static AppConfigResponse from(AppConfigEntity entity) {
        return new AppConfigResponse(entity.getKey(), entity.getValue(), entity.getDescription());
    }
}
