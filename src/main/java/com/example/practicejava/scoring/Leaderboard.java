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

import java.time.Instant;

@Entity
@Table(name = "leaderboard",
        uniqueConstraints = @UniqueConstraint(columnNames = {"season_id", "user_id"}))
public class Leaderboard extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int rank;

    @Column(name = "total_points", nullable = false)
    private int totalPoints;

    @Column(name = "last_calculated_at")
    private Instant lastCalculatedAt;

    protected Leaderboard() {}

    public Leaderboard(Season season, User user) {
        this.season = season;
        this.user = user;
    }

    public Season getSeason() { return season; }
    public User getUser() { return user; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public Instant getLastCalculatedAt() { return lastCalculatedAt; }
    public void setLastCalculatedAt(Instant lastCalculatedAt) { this.lastCalculatedAt = lastCalculatedAt; }
}
