package com.example.practicejava.match;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResultNotFoundException extends RuntimeException {
    public ResultNotFoundException(UUID matchId) {
        super("No result published for match " + matchId);
    }
}
