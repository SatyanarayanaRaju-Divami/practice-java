package com.example.practicejava.team.repository;

import com.example.practicejava.team.SeasonTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SeasonTeamRepository extends JpaRepository<SeasonTeam, UUID> {
    List<SeasonTeam> findBySeasonId(UUID seasonId);
    boolean existsBySeasonIdAndTeamId(UUID seasonId, UUID teamId);
    java.util.Optional<SeasonTeam> findBySeasonIdAndTeamId(UUID seasonId, UUID teamId);
    int countBySeasonId(UUID seasonId);
}
