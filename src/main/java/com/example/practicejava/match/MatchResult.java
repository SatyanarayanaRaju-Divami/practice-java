package com.example.practicejava.match;

import com.example.practicejava.common.BaseEntity;
import com.example.practicejava.player.Player;
import com.example.practicejava.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "match_results")
public class MatchResult extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false, unique = true)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_team_id")
    private Team winnerTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toss_winner_team_id")
    private Team tossWinnerTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_of_match_id")
    private Player playerOfMatch;

    @Column(name = "is_draw", nullable = false)
    private boolean isDraw = false;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "published_by")
    private UUID publishedBy;

    protected MatchResult() {}

    public MatchResult(Match match) {
        this.match = match;
    }

    public Match getMatch() { return match; }

    public Team getWinnerTeam() { return winnerTeam; }
    public void setWinnerTeam(Team winnerTeam) { this.winnerTeam = winnerTeam; }

    public Team getTossWinnerTeam() { return tossWinnerTeam; }
    public void setTossWinnerTeam(Team tossWinnerTeam) { this.tossWinnerTeam = tossWinnerTeam; }

    public Player getPlayerOfMatch() { return playerOfMatch; }
    public void setPlayerOfMatch(Player playerOfMatch) { this.playerOfMatch = playerOfMatch; }

    public boolean isDraw() { return isDraw; }
    public void setDraw(boolean draw) { isDraw = draw; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }

    public UUID getPublishedBy() { return publishedBy; }
    public void setPublishedBy(UUID publishedBy) { this.publishedBy = publishedBy; }
}
