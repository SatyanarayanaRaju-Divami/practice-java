package com.example.practicejava.match;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class MatchNotModifiableException extends RuntimeException {
    public MatchNotModifiableException(UUID matchId, MatchStatus status) {
        super("Match " + matchId + " cannot be modified — current status is " + status);
    }
}
