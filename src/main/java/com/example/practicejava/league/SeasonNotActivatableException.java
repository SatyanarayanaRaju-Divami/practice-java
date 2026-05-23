package com.example.practicejava.league;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class SeasonNotActivatableException extends RuntimeException {
    public SeasonNotActivatableException(String reason) {
        super("Season cannot be activated: " + reason);
    }
}
