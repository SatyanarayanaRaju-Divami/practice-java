package com.example.practicejava.team;

import com.example.practicejava.team.dto.CreateTeamRequest;
import com.example.practicejava.team.dto.TeamResponse;
import com.example.practicejava.team.dto.UpdateTeamRequest;
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
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public List<TeamResponse> list() {
        return teamService.findAll().stream().map(TeamResponse::from).toList();
    }

    @GetMapping("/{id}")
    public TeamResponse get(@PathVariable UUID id) {
        return TeamResponse.from(teamService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody CreateTeamRequest request,
                                                UriComponentsBuilder uriBuilder) {
        Team created = teamService.create(request);
        URI location = uriBuilder.path("/api/v1/teams/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(TeamResponse.from(created));
    }

    @PutMapping("/{id}")
    public TeamResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateTeamRequest request) {
        return TeamResponse.from(teamService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        teamService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
