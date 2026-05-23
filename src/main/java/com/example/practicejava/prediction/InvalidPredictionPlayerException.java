package com.example.practicejava.prediction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class InvalidPredictionPlayerException extends RuntimeException {
    public InvalidPredictionPlayerException(UUID playerId, UUID matchId) {
        super("Player " + playerId + " does not belong to either team in match " + matchId);
    }
}
