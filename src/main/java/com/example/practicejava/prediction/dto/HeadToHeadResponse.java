package com.example.practicejava.prediction.dto;

import com.example.practicejava.prediction.service.PredictionService;

public record HeadToHeadResponse(
        MatchPredictionResponse myPrediction,
        MatchPredictionResponse theirPrediction
) {
    public static HeadToHeadResponse from(PredictionService.HeadToHead h2h) {
        MatchPredictionResponse mine = h2h.mine() != null
                ? MatchPredictionResponse.from(h2h.mine(), h2h.result())
                : null;
        MatchPredictionResponse theirs = h2h.theirs() != null
                ? MatchPredictionResponse.from(h2h.theirs(), h2h.result())
                : null;
        return new HeadToHeadResponse(mine, theirs);
    }
}
