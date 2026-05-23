package com.example.practicejava.standing;

import com.example.practicejava.common.BaseEntity;
import com.example.practicejava.league.Season;
import com.example.practicejava.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "league_standings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"season_id", "team_id"}))
public class LeagueStanding extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "current_position")
    private Integer currentPosition;

    @Column(name = "matches_played", nullable = false)
    private int matchesPlayed = 0;

    @Column(nullable = false)
    private int wins = 0;

    @Column(nullable = false)
    private int draws = 0;

    @Column(nullable = false)
    private int losses = 0;

    @Column(name = "points_in_league", nullable = false)
    private int pointsInLeague = 0;

    protected LeagueStanding() {}

    public LeagueStanding(Season season, Team team) {
        this.season = season;
        this.team = team;
    }

    public Season getSeason() { return season; }
    public Team getTeam() { return team; }

    public Integer getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(Integer currentPosition) { this.currentPosition = currentPosition; }

    public int getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public int getPointsInLeague() { return pointsInLeague; }
    public void setPointsInLeague(int pointsInLeague) { this.pointsInLeague = pointsInLeague; }
}
