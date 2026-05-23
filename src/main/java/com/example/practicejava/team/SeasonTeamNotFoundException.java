package com.example.practicejava.team;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SeasonTeamNotFoundException extends RuntimeException {
    public SeasonTeamNotFoundException(UUID teamId, UUID seasonId) {
        super("Team " + teamId + " is not enrolled in season " + seasonId);
    }
}
