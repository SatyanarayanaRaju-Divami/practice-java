package com.example.practicejava.match;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class TeamNotInSeasonException extends RuntimeException {
    public TeamNotInSeasonException(UUID teamId, UUID seasonId) {
        super("Team " + teamId + " is not enrolled in season " + seasonId);
    }
}
