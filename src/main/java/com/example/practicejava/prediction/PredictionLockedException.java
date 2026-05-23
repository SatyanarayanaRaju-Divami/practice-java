package com.example.practicejava.prediction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PredictionLockedException extends RuntimeException {
    public PredictionLockedException(UUID matchId) {
        super("Prediction window for match " + matchId + " is closed");
    }
}
