package com.example.practicejava.match.repository;

import com.example.practicejava.match.Match;
import com.example.practicejava.match.MatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {
    List<Match> findBySeasonId(UUID seasonId);
    Page<Match> findBySeasonId(UUID seasonId, Pageable pageable);
    int countBySeasonId(UUID seasonId);

    @Query("SELECT MIN(m.scheduledAt) FROM Match m WHERE m.season.id = :seasonId")
    Optional<Instant> findEarliestScheduledAtBySeasonId(UUID seasonId);

    List<Match> findByLockTimeBetweenAndStatus(Instant from, Instant to, MatchStatus status);
    List<Match> findByLockTimeLessThanEqualAndStatus(Instant lockTime, MatchStatus status);
    List<Match> findByScheduledAtBeforeAndStatusNot(Instant before, MatchStatus status);
}
