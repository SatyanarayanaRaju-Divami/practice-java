package com.example.practicejava.scoring;

import com.example.practicejava.common.BaseEntity;
import com.example.practicejava.match.Match;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "score_breakdown")
public class ScoreBreakdown extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_id", nullable = false)
    private Score score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @Enumerated(EnumType.STRING)
    @Column(name = "prediction_type", nullable = false)
    private PredictionType predictionType;

    @Column(name = "points_earned", nullable = false)
    private int pointsEarned;

    protected ScoreBreakdown() {}

    public ScoreBreakdown(Score score, Match match, PredictionType predictionType, int pointsEarned) {
        this.score = score;
        this.match = match;
        this.predictionType = predictionType;
        this.pointsEarned = pointsEarned;
    }

    public Score getScore() { return score; }
    public Match getMatch() { return match; }
    public PredictionType getPredictionType() { return predictionType; }
    public int getPointsEarned() { return pointsEarned; }
}
