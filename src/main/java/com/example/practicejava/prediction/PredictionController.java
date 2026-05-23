package com.example.practicejava.prediction;

import com.example.practicejava.prediction.dto.LeaguePredictionResponse;
import com.example.practicejava.prediction.dto.MatchPredictionResponse;
import com.example.practicejava.prediction.dto.SubmitLeaguePredictionRequest;
import com.example.practicejava.prediction.dto.SubmitMatchPredictionRequest;
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
    public ResponseEntity<List<LeaguePredictionResponse>> submitLeaguePrediction(
            @PathVariable UUID seasonId,
            @RequestParam UUID userId,
            @Valid @RequestBody SubmitLeaguePredictionRequest request) {
        List<LeaguePrediction> predictions = predictionService.submitLeaguePrediction(
                seasonId, userId, request.predictions());
        return ResponseEntity.ok(predictions.stream().map(LeaguePredictionResponse::from).toList());
    }

    @GetMapping("/seasons/{seasonId}/predictions/league")
    public List<LeaguePredictionResponse> getLeaguePredictions(@PathVariable UUID seasonId) {
        return predictionService.getAllLeaguePredictions(seasonId)
                .stream().map(LeaguePredictionResponse::from).toList();
    }

    @PostMapping("/matches/{matchId}/predictions")
    public ResponseEntity<MatchPredictionResponse> submitMatchPrediction(
            @PathVariable UUID matchId,
            @RequestParam UUID userId,
            @Valid @RequestBody SubmitMatchPredictionRequest request) {
        MatchPrediction prediction = predictionService.submitMatchPrediction(matchId, userId, request);
        return ResponseEntity.ok(MatchPredictionResponse.from(prediction));
    }

    @GetMapping("/matches/{matchId}/predictions")
    public List<MatchPredictionResponse> getAllMatchPredictions(@PathVariable UUID matchId) {
        return predictionService.getAllMatchPredictions(matchId)
                .stream().map(MatchPredictionResponse::from).toList();
    }

    @GetMapping("/matches/{matchId}/predictions/me")
    public MatchPredictionResponse getMyMatchPrediction(@PathVariable UUID matchId,
                                                         @RequestParam UUID userId) {
        return MatchPredictionResponse.from(predictionService.getMyMatchPrediction(matchId, userId));
    }
}
