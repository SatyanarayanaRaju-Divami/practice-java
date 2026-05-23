package com.example.practicejava.prediction.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.match.MatchResult;
import com.example.practicejava.prediction.MatchPrediction;
import com.example.practicejava.prediction.dto.HeadToHeadResponse;
import com.example.practicejava.prediction.dto.LeaguePredictionResponse;
import com.example.practicejava.prediction.dto.MatchPredictionResponse;
import com.example.practicejava.prediction.dto.SubmitLeaguePredictionRequest;
import com.example.practicejava.prediction.dto.SubmitMatchPredictionRequest;
import com.example.practicejava.prediction.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Predictions", description = "Submit and view match and league predictions")
@RestController
@RequestMapping("/api/v1")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    // ─── US-017: Submit / update match prediction ────────────────────────────

    @Operation(summary = "Submit or update a match prediction")
    @PostMapping("/matches/{matchId}/predictions")
    public ResponseEntity<ApiResponse<MatchPredictionResponse>> submitMatchPrediction(
            @PathVariable UUID matchId,
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody SubmitMatchPredictionRequest request,
            HttpServletRequest req) {
        MatchPrediction prediction = predictionService.submitMatchPrediction(matchId, userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Match prediction submitted",
                MatchPredictionResponse.from(prediction), req.getRequestURI()));
    }

    // ─── US-018: View my prediction (empty state if not submitted) ───────────

    @Operation(summary = "Get my match prediction")
    @GetMapping("/matches/{matchId}/predictions/me")
    public ResponseEntity<ApiResponse<MatchPredictionResponse>> getMyMatchPrediction(
            @PathVariable UUID matchId,
            @AuthenticationPrincipal UUID userId,
            HttpServletRequest req) {
        MatchPredictionResponse response = predictionService.getMyMatchPrediction(matchId, userId)
                .map(MatchPredictionResponse::from)
                .orElse(null);
        return ResponseEntity.ok(ApiResponse.ok(response, req.getRequestURI()));
    }

    // ─── US-019 / US-020: All predictions after lock (paginated, with result) ─

    @Operation(summary = "Get all match predictions after lock (paginated, with result)")
    @GetMapping("/matches/{matchId}/predictions")
    public ResponseEntity<ApiResponse<Page<MatchPredictionResponse>>> getAllMatchPredictions(
            @PathVariable UUID matchId,
            @PageableDefault(size = 20, sort = "submittedAt") Pageable pageable,
            HttpServletRequest req) {
        Page<MatchPrediction> page = predictionService.getAllMatchPredictions(matchId, pageable);
        Optional<MatchResult> result = predictionService.getMatchResult(matchId);
        Page<MatchPredictionResponse> response = page.map(p -> MatchPredictionResponse.from(p, result));
        return ResponseEntity.ok(ApiResponse.ok(response, req.getRequestURI()));
    }

    // ─── League predictions (US-021 / US-022 stubs) ──────────────────────────

    @Operation(summary = "Submit or update league prediction (full season ranking)")
    @PostMapping("/seasons/{seasonId}/predictions/league")
    public ResponseEntity<ApiResponse<List<LeaguePredictionResponse>>> submitLeaguePrediction(
            @PathVariable UUID seasonId,
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody SubmitLeaguePredictionRequest request,
            HttpServletRequest req) {
        List<LeaguePredictionResponse> predictions = predictionService
                .submitLeaguePrediction(seasonId, userId, request.predictions())
                .stream().map(LeaguePredictionResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok("League prediction submitted", predictions, req.getRequestURI()));
    }

    @Operation(summary = "Get all league predictions after lock (paginated)")
    @GetMapping("/seasons/{seasonId}/predictions/league")
    public ResponseEntity<ApiResponse<Page<LeaguePredictionResponse>>> getLeaguePredictions(
            @PathVariable UUID seasonId,
            @PageableDefault(size = 20, sort = "predictedPosition") Pageable pageable,
            HttpServletRequest req) {
        Page<LeaguePredictionResponse> predictions = predictionService
                .getAllLeaguePredictions(seasonId, pageable)
                .map(LeaguePredictionResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(predictions, req.getRequestURI()));
    }

    // ─── US-020: Head-to-head comparison ─────────────────────────────────────

    @Operation(summary = "Compare your prediction with another user (head-to-head)")
    @GetMapping("/matches/{matchId}/predictions/head-to-head")
    public ResponseEntity<ApiResponse<HeadToHeadResponse>> headToHead(
            @PathVariable UUID matchId,
            @AuthenticationPrincipal UUID userId,
            @RequestParam("opponentId") UUID opponentId,
            HttpServletRequest req) {
        PredictionService.HeadToHead h2h = predictionService.getHeadToHead(matchId, userId, opponentId);
        HeadToHeadResponse response = HeadToHeadResponse.from(h2h);
        return ResponseEntity.ok(ApiResponse.ok(response, req.getRequestURI()));
    }
}
