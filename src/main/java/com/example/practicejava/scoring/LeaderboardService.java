package com.example.practicejava.scoring;

import com.example.practicejava.scoring.dto.LeaderboardEntryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardService(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> findBySeasonId(UUID seasonId) {
        return leaderboardRepository.findBySeasonIdOrderByRankAsc(seasonId)
                .stream().map(LeaderboardEntryResponse::from).toList();
    }
}
