package com.example.practicejava.appconfig;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AppConfigNotFoundException extends RuntimeException {
    public AppConfigNotFoundException(String key) {
        super("App config not found: " + key);
    }
}
