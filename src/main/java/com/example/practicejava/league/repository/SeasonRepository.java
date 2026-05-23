package com.example.practicejava.league.repository;

import com.example.practicejava.league.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SeasonRepository extends JpaRepository<Season, UUID> {
    List<Season> findByLeagueId(UUID leagueId);
}
