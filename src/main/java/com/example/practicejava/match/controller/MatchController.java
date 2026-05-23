package com.example.practicejava.match.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.match.Match;
import com.example.practicejava.match.MatchResult;
import com.example.practicejava.match.dto.CreateMatchRequest;
import com.example.practicejava.match.dto.MatchResponse;
import com.example.practicejava.match.dto.MatchResultResponse;
import com.example.practicejava.match.dto.PublishResultRequest;
import com.example.practicejava.match.dto.UpdateMatchRequest;
import com.example.practicejava.match.service.MatchService;
import com.example.practicejava.match.service.ResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Matches", description = "Schedule matches and publish results")
@RestController
@RequestMapping("/api/v1")
public class MatchController {

    private final MatchService matchService;
    private final ResultService resultService;

    public MatchController(MatchService matchService, ResultService resultService) {
        this.matchService = matchService;
        this.resultService = resultService;
    }

    @Operation(summary = "List matches for a season (paginated)")
    @GetMapping("/seasons/{seasonId}/matches")
    public ResponseEntity<ApiResponse<Page<MatchResponse>>> listBySeason(
            @PathVariable UUID seasonId,
            @PageableDefault(size = 20, sort = "scheduledAt") Pageable pageable,
            HttpServletRequest req) {
        Page<MatchResponse> matches = matchService.findBySeasonId(seasonId, pageable).map(MatchResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(matches, req.getRequestURI()));
    }

    @Operation(summary = "Schedule a new match (admin only)")
    @PostMapping("/seasons/{seasonId}/matches")
    public ResponseEntity<ApiResponse<MatchResponse>> create(@PathVariable UUID seasonId,
                                                              @Valid @RequestBody CreateMatchRequest request,
                                                              HttpServletRequest req) {
        Match created = matchService.create(seasonId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Match scheduled successfully", MatchResponse.from(created), req.getRequestURI()));
    }

    @Operation(summary = "Get a match by ID")
    @GetMapping("/matches/{id}")
    public ResponseEntity<ApiResponse<MatchResponse>> get(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(MatchResponse.from(matchService.findById(id)), req.getRequestURI()));
    }

    @Operation(summary = "Update match schedule (admin only)")
    @PutMapping("/matches/{id}")
    public ResponseEntity<ApiResponse<MatchResponse>> update(@PathVariable UUID id,
                                                              @Valid @RequestBody UpdateMatchRequest request,
                                                              HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(MatchResponse.from(matchService.update(id, request)), req.getRequestURI()));
    }

    @Operation(summary = "Delete a match (admin only)")
    @DeleteMapping("/matches/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id,
                                                     @AuthenticationPrincipal UUID deletedBy,
                                                     HttpServletRequest req) {
        matchService.delete(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }

    // ─── US-023: Publish match result (admin) ────────────────────────────────

    @Operation(summary = "Publish match result (admin only)")
    @PostMapping("/matches/{id}/result")
    public ResponseEntity<ApiResponse<MatchResultResponse>> publishResult(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID publishedBy,
            @Valid @RequestBody PublishResultRequest request,
            HttpServletRequest req) {
        MatchResult result = resultService.publishMatchResult(id, publishedBy, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Match result published", MatchResultResponse.from(result), req.getRequestURI()));
    }

    // ─── US-024: View match result ────────────────────────────────────────────

    @Operation(summary = "Get match result")
    @GetMapping("/matches/{id}/result")
    public ResponseEntity<ApiResponse<MatchResultResponse>> getResult(
            @PathVariable UUID id,
            HttpServletRequest req) {
        MatchResult result = resultService.getMatchResult(id);
        return ResponseEntity.ok(ApiResponse.ok(MatchResultResponse.from(result), req.getRequestURI()));
    }
}
