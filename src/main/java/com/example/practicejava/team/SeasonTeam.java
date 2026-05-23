package com.example.practicejava.team;

import com.example.practicejava.common.BaseEntity;
import com.example.practicejava.league.Season;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "season_teams",
        uniqueConstraints = @UniqueConstraint(columnNames = {"season_id", "team_id"}))
public class SeasonTeam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "seed_position")
    private Integer seedPosition;

    protected SeasonTeam() {}

    public SeasonTeam(Season season, Team team) {
        this.season = season;
        this.team = team;
    }

    public Season getSeason() { return season; }
    public Team getTeam() { return team; }

    public Integer getSeedPosition() { return seedPosition; }
    public void setSeedPosition(Integer seedPosition) { this.seedPosition = seedPosition; }
}
