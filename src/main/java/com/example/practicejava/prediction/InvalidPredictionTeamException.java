package com.example.practicejava.prediction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidPredictionTeamException extends RuntimeException {
    public InvalidPredictionTeamException(UUID teamId, UUID matchId) {
        super("Team " + teamId + " is not playing in match " + matchId);
    }
}
