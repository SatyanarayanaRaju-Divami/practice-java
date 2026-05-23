package com.example.practicejava.prediction;

import com.example.practicejava.common.BaseEntity;
import com.example.practicejava.match.Match;
import com.example.practicejava.team.Player;
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
@Table(name = "predictions_match",
        uniqueConstraints = @UniqueConstraint(columnNames = {"match_id", "user_id"}))
public class MatchPrediction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predicted_winner_team_id")
    private Team predictedWinnerTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predicted_toss_winner_id")
    private Team predictedTossWinner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predicted_potm_player_id")
    private Player predictedPotmPlayer;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    protected MatchPrediction() {}

    public MatchPrediction(Match match, User user) {
        this.match = match;
        this.user = user;
        this.submittedAt = Instant.now();
    }

    public Match getMatch() { return match; }
    public User getUser() { return user; }

    public Team getPredictedWinnerTeam() { return predictedWinnerTeam; }
    public void setPredictedWinnerTeam(Team predictedWinnerTeam) { this.predictedWinnerTeam = predictedWinnerTeam; }

    public Team getPredictedTossWinner() { return predictedTossWinner; }
    public void setPredictedTossWinner(Team predictedTossWinner) { this.predictedTossWinner = predictedTossWinner; }

    public Player getPredictedPotmPlayer() { return predictedPotmPlayer; }
    public void setPredictedPotmPlayer(Player predictedPotmPlayer) { this.predictedPotmPlayer = predictedPotmPlayer; }

    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
}
