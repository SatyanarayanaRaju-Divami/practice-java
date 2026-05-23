package com.example.practicejava.standing.repository;

import com.example.practicejava.standing.LeagueStanding;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeagueStandingRepository extends JpaRepository<LeagueStanding, UUID> {
    List<LeagueStanding> findBySeasonIdOrderByCurrentPositionAsc(UUID seasonId);
    Page<LeagueStanding> findBySeasonId(UUID seasonId, Pageable pageable);
    Optional<LeagueStanding> findBySeasonIdAndTeamId(UUID seasonId, UUID teamId);
}
