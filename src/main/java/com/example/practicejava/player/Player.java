package com.example.practicejava.player;

import com.example.practicejava.common.BaseEntity;
import com.example.practicejava.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "players")
@SQLRestriction("is_deleted = false")
public class Player extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(nullable = false)
    private String name;

    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    protected Player() {}

    public Player(Team team, String name) {
        this.team = team;
        this.teamName = team.getName();
        this.name = name;
    }

    public Team getTeam() { return team; }
    public void setTeam(Team team) {
        this.team = team;
        this.teamName = team.getName();
    }

    public String getTeamName() { return teamName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getJerseyNumber() { return jerseyNumber; }
    public void setJerseyNumber(Integer jerseyNumber) { this.jerseyNumber = jerseyNumber; }
}
