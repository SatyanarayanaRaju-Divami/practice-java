package com.example.practicejava.player.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.player.Player;
import com.example.practicejava.player.dto.CreatePlayerRequest;
import com.example.practicejava.player.dto.PlayerResponse;
import com.example.practicejava.player.dto.UpdatePlayerRequest;
import com.example.practicejava.player.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import java.util.UUID;

@Tag(name = "Players", description = "Manage players")
@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Operation(summary = "List all players (paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PlayerResponse>>> listAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            HttpServletRequest req) {
        Page<PlayerResponse> players = playerService.findAll(pageable).map(PlayerResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(players, req.getRequestURI()));
    }

    @Operation(summary = "List players by team (paginated)")
    @GetMapping("/by-team/{teamId}")
    public ResponseEntity<ApiResponse<Page<PlayerResponse>>> listByTeam(
            @PathVariable UUID teamId,
            @PageableDefault(size = 20, sort = "name") Pageable pageable,
            HttpServletRequest req) {
        Page<PlayerResponse> players = playerService.findByTeamId(teamId, pageable).map(PlayerResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(players, req.getRequestURI()));
    }

    @Operation(summary = "Get a player by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlayerResponse>> get(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(PlayerResponse.from(playerService.findById(id)), req.getRequestURI()));
    }

    @Operation(summary = "Create a new player (admin only)")
    @PostMapping
    public ResponseEntity<ApiResponse<PlayerResponse>> create(@Valid @RequestBody CreatePlayerRequest request,
                                                               HttpServletRequest req) {
        Player created = playerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Player created successfully", PlayerResponse.from(created), req.getRequestURI()));
    }

    @Operation(summary = "Update a player (admin only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlayerResponse>> update(@PathVariable UUID id,
                                                               @Valid @RequestBody UpdatePlayerRequest request,
                                                               HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(PlayerResponse.from(playerService.update(id, request)), req.getRequestURI()));
    }

    @Operation(summary = "Delete a player (admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, HttpServletRequest req) {
        playerService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }
}
