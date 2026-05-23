package com.example.practicejava.prediction;

import com.example.practicejava.common.BaseEntity;
import com.example.practicejava.league.Season;
import com.example.practicejava.team.Team;
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
@Table(name = "predictions_league",
        uniqueConstraints = @UniqueConstraint(columnNames = {"season_id", "user_id", "team_id"}))
public class LeaguePrediction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "predicted_position", nullable = false)
    private int predictedPosition;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    protected LeaguePrediction() {}

    public LeaguePrediction(Season season, User user, Team team, int predictedPosition) {
        this.season = season;
        this.user = user;
        this.team = team;
        this.predictedPosition = predictedPosition;
        this.submittedAt = Instant.now();
    }

    public Season getSeason() { return season; }
    public User getUser() { return user; }
    public Team getTeam() { return team; }

    public int getPredictedPosition() { return predictedPosition; }
    public void setPredictedPosition(int predictedPosition) { this.predictedPosition = predictedPosition; }

    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
}
