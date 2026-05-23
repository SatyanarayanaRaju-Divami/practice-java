package com.example.practicejava.scoring.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.scoring.dto.LeaderboardEntryResponse;
import com.example.practicejava.scoring.dto.MyRankResponse;
import com.example.practicejava.scoring.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Leaderboard", description = "Season leaderboard and personal rank")
@RestController
@RequestMapping("/api/v1/seasons/{seasonId}/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    // ─── US-026: Season leaderboard (paginated) ───────────────────────────────

    @Operation(summary = "Get season leaderboard (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<LeaderboardEntryResponse>>> list(
            @PathVariable UUID seasonId,
            @PageableDefault(size = 20, sort = "rank") Pageable pageable,
            HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(leaderboardService.findBySeasonId(seasonId, pageable), req.getRequestURI()));
    }

    // ─── US-027: My rank + breakdown ─────────────────────────────────────────

    @Operation(summary = "Get my rank and score breakdown")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyRankResponse>> myRank(
            @PathVariable UUID seasonId,
            @AuthenticationPrincipal UUID userId,
            HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(leaderboardService.findMyRank(seasonId, userId), req.getRequestURI()));
    }
}
