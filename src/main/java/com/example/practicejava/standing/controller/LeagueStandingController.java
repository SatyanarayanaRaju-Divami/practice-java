package com.example.practicejava.standing.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.standing.dto.LeagueStandingResponse;
import com.example.practicejava.standing.service.LeagueStandingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/seasons/{seasonId}/standings")
public class LeagueStandingController {

    private final LeagueStandingService leagueStandingService;

    public LeagueStandingController(LeagueStandingService leagueStandingService) {
        this.leagueStandingService = leagueStandingService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LeagueStandingResponse>>> list(@PathVariable UUID seasonId,
                                                                            HttpServletRequest req) {
        List<LeagueStandingResponse> standings = leagueStandingService.findBySeasonId(seasonId)
                .stream().map(LeagueStandingResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(standings, req.getRequestURI()));
    }
}
