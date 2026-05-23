package com.example.practicejava.league.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.league.Season;
import com.example.practicejava.league.dto.CreateSeasonRequest;
import com.example.practicejava.league.dto.SeasonResponse;
import com.example.practicejava.league.service.SeasonService;
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
public class SeasonController {

    private final SeasonService seasonService;

    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @GetMapping("/leagues/{leagueId}/seasons")
    public ResponseEntity<ApiResponse<List<SeasonResponse>>> listByLeague(@PathVariable UUID leagueId,
                                                                            HttpServletRequest req) {
        List<SeasonResponse> seasons = seasonService.findByLeagueId(leagueId).stream().map(SeasonResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(seasons, req.getRequestURI()));
    }

    @PostMapping("/leagues/{leagueId}/seasons")
    public ResponseEntity<ApiResponse<SeasonResponse>> create(@PathVariable UUID leagueId,
                                                               @Valid @RequestBody CreateSeasonRequest request,
                                                               HttpServletRequest req) {
        Season created = seasonService.create(leagueId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Season created successfully", SeasonResponse.from(created), req.getRequestURI()));
    }

    @GetMapping("/seasons/{id}")
    public ResponseEntity<ApiResponse<SeasonResponse>> get(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(SeasonResponse.from(seasonService.findById(id)), req.getRequestURI()));
    }

    @PutMapping("/seasons/{id}/activate")
    public ResponseEntity<ApiResponse<SeasonResponse>> activate(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Season activated", SeasonResponse.from(seasonService.activate(id)), req.getRequestURI()));
    }

    @PutMapping("/seasons/{id}/close")
    public ResponseEntity<ApiResponse<SeasonResponse>> close(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Season closed", SeasonResponse.from(seasonService.close(id)), req.getRequestURI()));
    }

    @DeleteMapping("/seasons/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, HttpServletRequest req) {
        seasonService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }
}
