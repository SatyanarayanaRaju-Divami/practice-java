package com.example.practicejava.match;

import com.example.practicejava.match.dto.CreateMatchRequest;
import com.example.practicejava.match.dto.MatchResponse;
import com.example.practicejava.match.dto.UpdateMatchRequest;
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
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/seasons/{seasonId}/matches")
    public List<MatchResponse> listBySeason(@PathVariable UUID seasonId) {
        return matchService.findBySeasonId(seasonId).stream().map(MatchResponse::from).toList();
    }

    @PostMapping("/seasons/{seasonId}/matches")
    public ResponseEntity<MatchResponse> create(@PathVariable UUID seasonId,
                                                 @Valid @RequestBody CreateMatchRequest request,
                                                 UriComponentsBuilder uriBuilder) {
        Match created = matchService.create(seasonId, request);
        URI location = uriBuilder.path("/api/v1/matches/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(MatchResponse.from(created));
    }

    @GetMapping("/matches/{id}")
    public MatchResponse get(@PathVariable UUID id) {
        return MatchResponse.from(matchService.findById(id));
    }

    @PutMapping("/matches/{id}")
    public MatchResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateMatchRequest request) {
        return MatchResponse.from(matchService.update(id, request));
    }

    @DeleteMapping("/matches/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        matchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
