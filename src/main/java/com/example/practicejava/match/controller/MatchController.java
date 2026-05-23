package com.example.practicejava.match.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.match.Match;
import com.example.practicejava.match.dto.CreateMatchRequest;
import com.example.practicejava.match.dto.MatchResponse;
import com.example.practicejava.match.dto.UpdateMatchRequest;
import com.example.practicejava.match.service.MatchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/seasons/{seasonId}/matches")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> listBySeason(@PathVariable UUID seasonId,
                                                                           HttpServletRequest req) {
        List<MatchResponse> matches = matchService.findBySeasonId(seasonId).stream().map(MatchResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(matches, req.getRequestURI()));
    }

    @PostMapping("/seasons/{seasonId}/matches")
    public ResponseEntity<ApiResponse<MatchResponse>> create(@PathVariable UUID seasonId,
                                                              @Valid @RequestBody CreateMatchRequest request,
                                                              HttpServletRequest req) {
        Match created = matchService.create(seasonId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Match scheduled successfully", MatchResponse.from(created), req.getRequestURI()));
    }

    @GetMapping("/matches/{id}")
    public ResponseEntity<ApiResponse<MatchResponse>> get(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(MatchResponse.from(matchService.findById(id)), req.getRequestURI()));
    }

    @PutMapping("/matches/{id}")
    public ResponseEntity<ApiResponse<MatchResponse>> update(@PathVariable UUID id,
                                                              @Valid @RequestBody UpdateMatchRequest request,
                                                              HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(MatchResponse.from(matchService.update(id, request)), req.getRequestURI()));
    }

    @DeleteMapping("/matches/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, HttpServletRequest req) {
        matchService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }
}
