package com.example.practicejava.prediction.dto;

import java.util.List;
import java.util.UUID;

public record SubmitLeaguePredictionRequest(List<Entry> predictions) {
    public record Entry(UUID teamId, int predictedPosition) {
    }
}
