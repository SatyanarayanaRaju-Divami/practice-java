package com.example.practicejava.scoring.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.scoring.dto.LeaderboardEntryResponse;
import com.example.practicejava.scoring.service.LeaderboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seasons/{seasonId}/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaderboardEntryResponse>>> list(@PathVariable UUID seasonId,
                                                                              HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(leaderboardService.findBySeasonId(seasonId), req.getRequestURI()));
    }
}
