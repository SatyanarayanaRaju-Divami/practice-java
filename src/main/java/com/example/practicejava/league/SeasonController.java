package com.example.practicejava.league;

import com.example.practicejava.league.dto.CreateSeasonRequest;
import com.example.practicejava.league.dto.SeasonResponse;
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
@RequestMapping("/api/v1")
public class SeasonController {

    private final SeasonService seasonService;

    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @GetMapping("/leagues/{leagueId}/seasons")
    public List<SeasonResponse> listByLeague(@PathVariable UUID leagueId) {
        return seasonService.findByLeagueId(leagueId).stream().map(SeasonResponse::from).toList();
    }

    @PostMapping("/leagues/{leagueId}/seasons")
    public ResponseEntity<SeasonResponse> create(@PathVariable UUID leagueId,
                                                  @Valid @RequestBody CreateSeasonRequest request,
                                                  UriComponentsBuilder uriBuilder) {
        Season created = seasonService.create(leagueId, request);
        URI location = uriBuilder.path("/api/v1/seasons/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(SeasonResponse.from(created));
    }

    @GetMapping("/seasons/{id}")
    public SeasonResponse get(@PathVariable UUID id) {
        return SeasonResponse.from(seasonService.findById(id));
    }

    @PutMapping("/seasons/{id}/activate")
    public SeasonResponse activate(@PathVariable UUID id) {
        return SeasonResponse.from(seasonService.activate(id));
    }

    @PutMapping("/seasons/{id}/close")
    public SeasonResponse close(@PathVariable UUID id) {
        return SeasonResponse.from(seasonService.close(id));
    }

    @DeleteMapping("/seasons/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        seasonService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
