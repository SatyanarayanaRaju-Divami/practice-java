package com.example.practicejava.prediction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PredictionWindowOpenException extends RuntimeException {
    public PredictionWindowOpenException(UUID matchId) {
        super("Predictions for match " + matchId + " are not visible until the prediction window closes");
    }
}
