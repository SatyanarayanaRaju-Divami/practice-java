package com.example.practicejava.league.repository;

import com.example.practicejava.league.Season;
import com.example.practicejava.league.SeasonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SeasonRepository extends JpaRepository<Season, UUID> {
    List<Season> findByLeagueId(UUID leagueId);
    Page<Season> findByLeagueId(UUID leagueId, Pageable pageable);
    List<Season> findByLeagueLockTimeLessThanEqualAndStatus(Instant lockTime, SeasonStatus status);
}
