package com.example.practicejava.league;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SeasonNotFoundException extends RuntimeException {
    public SeasonNotFoundException(UUID id) {
        super("Season not found: " + id);
    }
}
