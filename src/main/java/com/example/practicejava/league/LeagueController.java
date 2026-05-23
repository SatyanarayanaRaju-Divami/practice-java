package com.example.practicejava.league;

import com.example.practicejava.league.dto.CreateLeagueRequest;
import com.example.practicejava.league.dto.LeagueResponse;
import com.example.practicejava.league.dto.UpdateLeagueRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leagues")
public class LeagueController {

    private final LeagueService leagueService;

    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping
    public List<LeagueResponse> list() {
        return leagueService.findAll().stream().map(LeagueResponse::from).toList();
    }

    @GetMapping("/{id}")
    public LeagueResponse get(@PathVariable UUID id) {
        return LeagueResponse.from(leagueService.findById(id));
    }

    @PostMapping
    public ResponseEntity<LeagueResponse> create(@Valid @RequestBody CreateLeagueRequest request,
                                                  UriComponentsBuilder uriBuilder) {
        League created = leagueService.create(request);
        URI location = uriBuilder.path("/api/v1/leagues/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(LeagueResponse.from(created));
    }

    @PutMapping("/{id}")
    public LeagueResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateLeagueRequest request) {
        return LeagueResponse.from(leagueService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        leagueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
