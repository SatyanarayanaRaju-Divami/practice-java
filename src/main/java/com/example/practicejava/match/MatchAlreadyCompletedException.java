package com.example.practicejava.match;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class MatchAlreadyCompletedException extends RuntimeException {
    public MatchAlreadyCompletedException(UUID matchId) {
        super("Match " + matchId + " already has a published result");
    }
}
