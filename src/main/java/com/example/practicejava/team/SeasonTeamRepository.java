package com.example.practicejava.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SeasonTeamRepository extends JpaRepository<SeasonTeam, UUID> {
    List<SeasonTeam> findBySeasonId(UUID seasonId);
    boolean existsBySeasonIdAndTeamId(UUID seasonId, UUID teamId);
}
