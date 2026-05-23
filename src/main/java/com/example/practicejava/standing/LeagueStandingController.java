package com.example.practicejava.standing;

import com.example.practicejava.standing.dto.LeagueStandingResponse;
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
    public List<LeagueStandingResponse> list(@PathVariable UUID seasonId) {
        return leagueStandingService.findBySeasonId(seasonId).stream().map(LeagueStandingResponse::from).toList();
    }
}
