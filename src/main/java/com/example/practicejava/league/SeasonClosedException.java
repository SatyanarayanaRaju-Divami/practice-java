package com.example.practicejava.league;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class SeasonClosedException extends RuntimeException {
    public SeasonClosedException(UUID seasonId) {
        super("Season " + seasonId + " is closed and cannot be modified");
    }
}
