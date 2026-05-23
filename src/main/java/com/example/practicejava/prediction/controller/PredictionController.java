package com.example.practicejava.prediction.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.prediction.MatchPrediction;
import com.example.practicejava.prediction.dto.LeaguePredictionResponse;
import com.example.practicejava.prediction.dto.MatchPredictionResponse;
import com.example.practicejava.prediction.dto.SubmitLeaguePredictionRequest;
import com.example.practicejava.prediction.dto.SubmitMatchPredictionRequest;
import com.example.practicejava.prediction.service.PredictionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/seasons/{seasonId}/predictions/league")
    public ResponseEntity<ApiResponse<List<LeaguePredictionResponse>>> submitLeaguePrediction(
            @PathVariable UUID seasonId,
            @RequestParam UUID userId,
            @Valid @RequestBody SubmitLeaguePredictionRequest request,
            HttpServletRequest req) {
        List<LeaguePredictionResponse> predictions = predictionService
                .submitLeaguePrediction(seasonId, userId, request.predictions())
                .stream().map(LeaguePredictionResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok("League prediction submitted", predictions, req.getRequestURI()));
    }

    @GetMapping("/seasons/{seasonId}/predictions/league")
    public ResponseEntity<ApiResponse<List<LeaguePredictionResponse>>> getLeaguePredictions(
            @PathVariable UUID seasonId, HttpServletRequest req) {
        List<LeaguePredictionResponse> predictions = predictionService.getAllLeaguePredictions(seasonId)
                .stream().map(LeaguePredictionResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(predictions, req.getRequestURI()));
    }

    @PostMapping("/matches/{matchId}/predictions")
    public ResponseEntity<ApiResponse<MatchPredictionResponse>> submitMatchPrediction(
            @PathVariable UUID matchId,
            @RequestParam UUID userId,
            @Valid @RequestBody SubmitMatchPredictionRequest request,
            HttpServletRequest req) {
        MatchPrediction prediction = predictionService.submitMatchPrediction(matchId, userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Match prediction submitted",
                MatchPredictionResponse.from(prediction), req.getRequestURI()));
    }

    @GetMapping("/matches/{matchId}/predictions")
    public ResponseEntity<ApiResponse<List<MatchPredictionResponse>>> getAllMatchPredictions(
            @PathVariable UUID matchId, HttpServletRequest req) {
        List<MatchPredictionResponse> predictions = predictionService.getAllMatchPredictions(matchId)
                .stream().map(MatchPredictionResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(predictions, req.getRequestURI()));
    }

    @GetMapping("/matches/{matchId}/predictions/me")
    public ResponseEntity<ApiResponse<MatchPredictionResponse>> getMyMatchPrediction(
            @PathVariable UUID matchId, @RequestParam UUID userId, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(
                MatchPredictionResponse.from(predictionService.getMyMatchPrediction(matchId, userId)),
                req.getRequestURI()));
    }
}
