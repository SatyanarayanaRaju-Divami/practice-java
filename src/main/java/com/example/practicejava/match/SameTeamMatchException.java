package com.example.practicejava.match;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SameTeamMatchException extends RuntimeException {
    public SameTeamMatchException() {
        super("Home team and away team cannot be the same");
    }
}
