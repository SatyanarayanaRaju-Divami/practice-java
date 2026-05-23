package com.example.practicejava.match.repository;

import com.example.practicejava.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {
    List<Match> findBySeasonId(UUID seasonId);
}
