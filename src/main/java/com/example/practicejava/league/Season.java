package com.example.practicejava.league;

import com.example.practicejava.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@Entity
@Table(name = "seasons")
@SQLRestriction("is_deleted = false")
public class Season extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", nullable = false)
    private League league;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeasonStatus status = SeasonStatus.DRAFT;

    @Column(name = "first_match_start_time")
    private Instant firstMatchStartTime;

    @Column(name = "league_lock_time")
    private Instant leagueLockTime;

    protected Season() {}

    public Season(League league, String name) {
        this.league = league;
        this.name = name;
    }

    public League getLeague() { return league; }
    public void setLeague(League league) { this.league = league; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public SeasonStatus getStatus() { return status; }
    public void setStatus(SeasonStatus status) { this.status = status; }

    public Instant getFirstMatchStartTime() { return firstMatchStartTime; }
    public void setFirstMatchStartTime(Instant firstMatchStartTime) { this.firstMatchStartTime = firstMatchStartTime; }

    public Instant getLeagueLockTime() { return leagueLockTime; }
    public void setLeagueLockTime(Instant leagueLockTime) { this.leagueLockTime = leagueLockTime; }
}
