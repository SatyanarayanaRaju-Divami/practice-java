package com.example.practicejava.team.service;

import com.example.practicejava.league.Season;
import com.example.practicejava.league.service.SeasonService;
import com.example.practicejava.team.SeasonTeam;
import com.example.practicejava.team.SeasonTeamNotFoundException;
import com.example.practicejava.team.Team;
import com.example.practicejava.team.TeamAlreadyEnrolledException;
import com.example.practicejava.team.dto.EnrollTeamRequest;
import com.example.practicejava.team.repository.SeasonTeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SeasonTeamService {

    private final SeasonTeamRepository seasonTeamRepository;
    private final SeasonService seasonService;
    private final TeamService teamService;

    public SeasonTeamService(SeasonTeamRepository seasonTeamRepository,
                              SeasonService seasonService,
                              TeamService teamService) {
        this.seasonTeamRepository = seasonTeamRepository;
        this.seasonService = seasonService;
        this.teamService = teamService;
    }

    @Transactional(readOnly = true)
    public List<SeasonTeam> findBySeasonId(UUID seasonId) {
        seasonService.findById(seasonId); // validate season exists
        return seasonTeamRepository.findBySeasonId(seasonId);
    }

    public SeasonTeam enroll(UUID seasonId, EnrollTeamRequest request) {
        Season season = seasonService.findById(seasonId);
        Team team = teamService.findById(request.teamId());
        if (seasonTeamRepository.existsBySeasonIdAndTeamId(seasonId, request.teamId())) {
            throw new TeamAlreadyEnrolledException(request.teamId(), seasonId);
        }
        SeasonTeam seasonTeam = new SeasonTeam(season, team);
        seasonTeam.setSeedPosition(request.seedPosition());
        return seasonTeamRepository.save(seasonTeam);
    }

    public void unenroll(UUID seasonId, UUID teamId, UUID deletedBy) {
        SeasonTeam seasonTeam = seasonTeamRepository.findBySeasonIdAndTeamId(seasonId, teamId)
                .orElseThrow(() -> new SeasonTeamNotFoundException(teamId, seasonId));
        seasonTeam.softDelete(deletedBy);
    }
}
