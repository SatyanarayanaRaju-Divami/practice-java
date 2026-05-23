package com.example.practicejava.scoring.service;

import com.example.practicejava.league.service.SeasonService;
import com.example.practicejava.scoring.Leaderboard;
import com.example.practicejava.scoring.dto.LeaderboardEntryResponse;
import com.example.practicejava.scoring.dto.MyRankResponse;
import com.example.practicejava.scoring.repository.LeaderboardRepository;
import com.example.practicejava.scoring.repository.ScoreBreakdownRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;
    private final ScoreBreakdownRepository scoreBreakdownRepository;
    private final SeasonService seasonService;

    public LeaderboardService(LeaderboardRepository leaderboardRepository,
                              ScoreBreakdownRepository scoreBreakdownRepository,
                              SeasonService seasonService) {
        this.leaderboardRepository = leaderboardRepository;
        this.scoreBreakdownRepository = scoreBreakdownRepository;
        this.seasonService = seasonService;
    }

    // ─── US-026: Season leaderboard (paginated) ───────────────────────────────

    @Transactional(readOnly = true)
    public Page<LeaderboardEntryResponse> findBySeasonId(UUID seasonId, Pageable pageable) {
        seasonService.findById(seasonId); // validate exists
        return leaderboardRepository.findBySeasonId(seasonId, pageable)
                .map(LeaderboardEntryResponse::from);
    }

    // ─── US-027: My rank + score breakdown ───────────────────────────────────

    @Transactional(readOnly = true)
    public MyRankResponse findMyRank(UUID seasonId, UUID userId) {
        seasonService.findById(seasonId); // validate exists
        Leaderboard entry = leaderboardRepository
                .findBySeasonIdAndUserId(seasonId, userId)
                .orElse(null);
        if (entry == null) {
            return null; // not yet on the board
        }
        return MyRankResponse.from(entry,
                scoreBreakdownRepository.findByScoreSeasonIdAndScoreUserId(seasonId, userId));
    }
}
