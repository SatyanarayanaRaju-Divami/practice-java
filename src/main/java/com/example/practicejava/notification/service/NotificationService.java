package com.example.practicejava.notification.service;

import com.example.practicejava.match.Match;
import com.example.practicejava.match.MatchResult;
import com.example.practicejava.notification.EmailEventType;
import com.example.practicejava.notification.repository.EmailLogRepository;
import com.example.practicejava.scoring.ScoreBreakdown;
import com.example.practicejava.scoring.repository.ScoreBreakdownRepository;
import com.example.practicejava.scoring.repository.ScoreRepository;
import com.example.practicejava.user.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm 'UTC'").withZone(ZoneId.of("UTC"));

    private final EmailService emailService;
    private final EmailLogRepository emailLogRepository;
    private final ScoreBreakdownRepository scoreBreakdownRepository;
    private final ScoreRepository scoreRepository;

    public NotificationService(EmailService emailService,
                               EmailLogRepository emailLogRepository,
                               ScoreBreakdownRepository scoreBreakdownRepository,
                               ScoreRepository scoreRepository) {
        this.emailService = emailService;
        this.emailLogRepository = emailLogRepository;
        this.scoreBreakdownRepository = scoreBreakdownRepository;
        this.scoreRepository = scoreRepository;
    }

    // ─── US-028: Match prediction reminder ───────────────────────────────────

    public void sendMatchReminder(User user, Match match) {
        String subject = "Reminder: Submit your prediction for "
                + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName();
        String body = "Hi " + user.getDisplayName() + ",\n\n"
                + "The prediction window for the match below is closing soon.\n\n"
                + "Match: " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName() + "\n"
                + "Scheduled: " + FMT.format(match.getScheduledAt()) + "\n"
                + "Prediction deadline: " + FMT.format(match.getLockTime()) + "\n\n"
                + "Log in to Family League to submit your prediction before it's too late!\n\n"
                + "— Family League";
        emailService.sendAndLog(user, EmailEventType.MATCH_REMINDER.name(), subject, body, match.getId());
    }

    // ─── US-029: Admin pending result alert ──────────────────────────────────

    public void sendPendingResultAlert(User admin, List<Match> pendingMatches) {
        String subject = "Action Required: " + pendingMatches.size() + " match(es) awaiting result entry";
        StringBuilder body = new StringBuilder("Hi ").append(admin.getDisplayName()).append(",\n\n")
                .append("The following match(es) have ended but no result has been published:\n\n");
        for (Match m : pendingMatches) {
            body.append("• ").append(m.getHomeTeam().getName())
                    .append(" vs ").append(m.getAwayTeam().getName())
                    .append(" (scheduled: ").append(FMT.format(m.getScheduledAt())).append(")\n");
        }
        body.append("\nPlease log in to Family League and publish the results.\n\n— Family League");
        // referenceId not used here — one email covers all pending matches
        emailService.sendAndLog(admin, EmailEventType.PENDING_RESULT_ALERT.name(), subject, body.toString());
    }

    // ─── US-030: Score update email after result publish ─────────────────────

    @Async("scoreExecutor")
    public void sendScoreUpdateEmails(MatchResult result, List<UUID> userIds) {
        for (UUID userId : userIds) {
            scoreRepository.findBySeasonIdAndUserId(result.getMatch().getSeason().getId(), userId)
                    .ifPresent(score -> {
                        List<ScoreBreakdown> breakdowns = scoreBreakdownRepository
                                .findByScoreSeasonIdAndScoreUserId(
                                        result.getMatch().getSeason().getId(), userId);
                        // only breakdowns earned in this match
                        List<ScoreBreakdown> forThisMatch = breakdowns.stream()
                                .filter(b -> b.getMatch() != null
                                        && b.getMatch().getId().equals(result.getMatch().getId()))
                                .toList();
                        int pointsThisMatch = forThisMatch.stream()
                                .mapToInt(ScoreBreakdown::getPointsEarned).sum();

                        String subject = "Your score for "
                                + result.getMatch().getHomeTeam().getName()
                                + " vs " + result.getMatch().getAwayTeam().getName();
                        StringBuilder body = new StringBuilder("Hi ").append(score.getUser().getDisplayName())
                                .append(",\n\nThe result has been published! Here's how you scored:\n\n");
                        for (ScoreBreakdown b : forThisMatch) {
                            body.append("• ").append(b.getPredictionType().name())
                                    .append(": +").append(b.getPointsEarned()).append(" pt(s)\n");
                        }
                        body.append("\nPoints earned this match: ").append(pointsThisMatch)
                                .append("\nTotal season points: ").append(score.getTotalPoints())
                                .append("\n\n— Family League");
                        emailService.sendAndLog(score.getUser(), EmailEventType.SCORE_UPDATE.name(),
                                subject, body.toString(), result.getMatch().getId());
                    });
        }
    }

    // ─── US-031: Leaderboard recap email for admins ───────────────────────────

    @Async("scoreExecutor")
    public void sendLeaderboardRecapToAdmins(List<User> admins, String seasonName,
                                              Match match, int usersUpdated) {
        String subject = "Leaderboard updated — " + seasonName;
        String body = "Hi Admin,\n\n"
                + "The leaderboard for " + seasonName + " has been recalculated.\n\n"
                + "Match: " + match.getHomeTeam().getName() + " vs " + match.getAwayTeam().getName() + "\n"
                + "Users scored: " + usersUpdated + "\n\n"
                + "— Family League";
        for (User admin : admins) {
            emailService.sendAndLog(admin, EmailEventType.LEADERBOARD_RECAP.name(),
                    subject, body, match.getId());
        }
    }

    // ─── US-032: Bulk notification ────────────────────────────────────────────

    @Async("scoreExecutor")
    public void sendBulk(List<User> recipients, String eventType, String subject, String body) {
        for (User user : recipients) {
            emailService.sendAndLog(user, eventType, subject, body);
        }
    }
}
