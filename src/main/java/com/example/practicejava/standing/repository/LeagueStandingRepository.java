package com.example.practicejava.standing.repository;

import com.example.practicejava.standing.LeagueStanding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeagueStandingRepository extends JpaRepository<LeagueStanding, UUID> {
    List<LeagueStanding> findBySeasonIdOrderByCurrentPositionAsc(UUID seasonId);
}
