package com.example.practicejava.scoring;

import com.example.practicejava.common.BaseEntity;
import com.example.practicejava.league.Season;
import com.example.practicejava.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "scores",
        uniqueConstraints = @UniqueConstraint(columnNames = {"season_id", "user_id"}))
public class Score extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Written only by ScoreCalculationService — never via API
    @Column(name = "total_points", nullable = false)
    private int totalPoints = 0;

    protected Score() {}

    public Score(Season season, User user) {
        this.season = season;
        this.user = user;
    }

    public Season getSeason() { return season; }
    public User getUser() { return user; }

    public int getTotalPoints() { return totalPoints; }
    public void addPoints(int points) { this.totalPoints += points; }
}
