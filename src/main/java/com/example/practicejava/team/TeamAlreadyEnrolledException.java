package com.example.practicejava.team;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class TeamAlreadyEnrolledException extends RuntimeException {
    public TeamAlreadyEnrolledException(UUID teamId, UUID seasonId) {
        super("Team " + teamId + " is already enrolled in season " + seasonId);
    }
}
