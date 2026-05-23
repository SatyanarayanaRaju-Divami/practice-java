package com.example.practicejava.team;

import com.example.practicejava.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
public class Player extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private String name;

    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    protected Player() {}

    public Player(Team team, String name) {
        this.team = team;
        this.name = name;
    }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getJerseyNumber() { return jerseyNumber; }
    public void setJerseyNumber(Integer jerseyNumber) { this.jerseyNumber = jerseyNumber; }
}
