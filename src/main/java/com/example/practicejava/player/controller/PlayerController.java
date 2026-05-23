package com.example.practicejava.player.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.player.Player;
import com.example.practicejava.player.dto.CreatePlayerRequest;
import com.example.practicejava.player.dto.PlayerResponse;
import com.example.practicejava.player.dto.UpdatePlayerRequest;
import com.example.practicejava.player.service.PlayerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ApiResponse<List<PlayerResponse>>> listAll(HttpServletRequest req) {
        List<PlayerResponse> players = playerService.findAll().stream().map(PlayerResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(players, req.getRequestURI()));
    }

    @GetMapping("/by-team/{teamId}")
    public ResponseEntity<ApiResponse<List<PlayerResponse>>> listByTeam(@PathVariable UUID teamId,
                                                                          HttpServletRequest req) {
        List<PlayerResponse> players = playerService.findByTeamId(teamId).stream().map(PlayerResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(players, req.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlayerResponse>> get(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(PlayerResponse.from(playerService.findById(id)), req.getRequestURI()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PlayerResponse>> create(@Valid @RequestBody CreatePlayerRequest request,
                                                               HttpServletRequest req) {
        Player created = playerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Player created successfully", PlayerResponse.from(created), req.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlayerResponse>> update(@PathVariable UUID id,
                                                               @Valid @RequestBody UpdatePlayerRequest request,
                                                               HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(PlayerResponse.from(playerService.update(id, request)), req.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, HttpServletRequest req) {
        playerService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }
}
