package com.example.practicejava.league;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class SeasonNotClosableException extends RuntimeException {
    public SeasonNotClosableException(UUID seasonId) {
        super("Season " + seasonId + " can only be closed when in COMPLETED status");
    }
}
