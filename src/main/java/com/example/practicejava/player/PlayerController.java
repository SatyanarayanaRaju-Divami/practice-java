package com.example.practicejava.player;

import com.example.practicejava.player.dto.CreatePlayerRequest;
import com.example.practicejava.player.dto.PlayerResponse;
import com.example.practicejava.player.dto.UpdatePlayerRequest;
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
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public List<PlayerResponse> listAll() {
        return playerService.findAll().stream().map(PlayerResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> create(@Valid @RequestBody CreatePlayerRequest request,
                                                  UriComponentsBuilder uriBuilder) {
        Player created = playerService.create(request);
        URI location = uriBuilder.path("/api/v1/players/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(PlayerResponse.from(created));
    }

    @GetMapping("/{id}")
    public PlayerResponse get(@PathVariable UUID id) {
        return PlayerResponse.from(playerService.findById(id));
    }

    @PutMapping("/{id}")
    public PlayerResponse update(@PathVariable UUID id,
                                  @Valid @RequestBody UpdatePlayerRequest request) {
        return PlayerResponse.from(playerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        playerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Convenience: list players by team
    @GetMapping("/by-team/{teamId}")
    public List<PlayerResponse> listByTeam(@PathVariable UUID teamId) {
        return playerService.findByTeamId(teamId).stream().map(PlayerResponse::from).toList();
    }
}
