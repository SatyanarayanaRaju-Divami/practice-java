package com.example.practicejava.match.repository;

import com.example.practicejava.match.MatchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MatchResultRepository extends JpaRepository<MatchResult, UUID> {
    Optional<MatchResult> findByMatchId(UUID matchId);
}
