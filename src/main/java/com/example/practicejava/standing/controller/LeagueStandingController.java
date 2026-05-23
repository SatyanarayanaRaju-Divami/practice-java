package com.example.practicejava.standing.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.standing.dto.LeagueStandingResponse;
import com.example.practicejava.standing.service.LeagueStandingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Standings", description = "League standings within a season")
@RestController
@RequestMapping("/api/v1/seasons/{seasonId}/standings")
public class LeagueStandingController {

    private final LeagueStandingService leagueStandingService;

    public LeagueStandingController(LeagueStandingService leagueStandingService) {
        this.leagueStandingService = leagueStandingService;
    }

    @Operation(summary = "Get league standings for a season (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<LeagueStandingResponse>>> list(
            @PathVariable UUID seasonId,
            @PageableDefault(size = 20, sort = "currentPosition") Pageable pageable,
            HttpServletRequest req) {
        Page<LeagueStandingResponse> standings = leagueStandingService.findBySeasonId(seasonId, pageable)
                .map(LeagueStandingResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(standings, req.getRequestURI()));
    }
}
