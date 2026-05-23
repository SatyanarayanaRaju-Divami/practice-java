package com.example.practicejava.notification.service;

import com.example.practicejava.appconfig.service.AppConfigService;
import com.example.practicejava.league.Season;
import com.example.practicejava.league.SeasonStatus;
import com.example.practicejava.league.repository.SeasonRepository;
import com.example.practicejava.match.Match;
import com.example.practicejava.match.MatchStatus;
import com.example.practicejava.match.repository.MatchRepository;
import com.example.practicejava.match.repository.MatchResultRepository;
import com.example.practicejava.notification.EmailEventType;
import com.example.practicejava.notification.repository.EmailLogRepository;
import com.example.practicejava.prediction.MatchPrediction;
import com.example.practicejava.prediction.repository.MatchPredictionRepository;
import com.example.practicejava.user.User;
import com.example.practicejava.user.UserRole;
import com.example.practicejava.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScheduledNotificationService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledNotificationService.class);

    private final MatchRepository matchRepository;
    private final MatchResultRepository matchResultRepository;
    private final MatchPredictionRepository matchPredictionRepository;
    private final UserRepository userRepository;
    private final EmailLogRepository emailLogRepository;
    private final NotificationService notificationService;
    private final AppConfigService appConfigService;
    private final SeasonRepository seasonRepository;

    public ScheduledNotificationService(MatchRepository matchRepository,
                                         MatchResultRepository matchResultRepository,
                                         MatchPredictionRepository matchPredictionRepository,
                                         UserRepository userRepository,
                                         EmailLogRepository emailLogRepository,
                                         NotificationService notificationService,
                                         AppConfigService appConfigService,
                                         SeasonRepository seasonRepository) {
        this.matchRepository = matchRepository;
        this.matchResultRepository = matchResultRepository;
        this.matchPredictionRepository = matchPredictionRepository;
        this.userRepository = userRepository;
        this.emailLogRepository = emailLogRepository;
        this.notificationService = notificationService;
        this.appConfigService = appConfigService;
        this.seasonRepository = seasonRepository;
    }

    // ─── US-028: Match prediction reminder (runs every minute) ───────────────

    @Scheduled(fixedDelay = 60_000)
    @Transactional(readOnly = true)
    public void sendMatchPredictionReminders() {
        long offsetHours = Long.parseLong(
                appConfigService.findByKey("match.reminder.offset.hours").getValue());

        Instant windowStart = Instant.now();
        Instant windowEnd = Instant.now().plus(offsetHours, ChronoUnit.HOURS);

        List<Match> upcomingMatches = matchRepository
                .findByLockTimeBetweenAndStatus(windowStart, windowEnd, MatchStatus.SCHEDULED);

        if (upcomingMatches.isEmpty()) return;

        List<User> allActiveUsers = userRepository.findByIsActiveTrue();

        for (Match match : upcomingMatches) {
            Set<UUID> predictedUserIds = matchPredictionRepository.findByMatchId(match.getId())
                    .stream().map(p -> p.getUser().getId()).collect(Collectors.toSet());

            for (User user : allActiveUsers) {
                if (predictedUserIds.contains(user.getId())) continue;

                // Idempotency: skip if reminder already sent for this match+user
                if (emailLogRepository.existsByEventTypeAndReferenceIdAndRecipientEmail(
                        EmailEventType.MATCH_REMINDER.name(), match.getId(), user.getEmail())) continue;

                log.debug("Sending match reminder: user={} match={}", user.getEmail(), match.getId());
                notificationService.sendMatchReminder(user, match);
            }
        }
    }

    // ─── US-034: Auto-lock expired matches (runs every minute) ──────────────

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void lockExpiredMatches() {
        List<Match> toLock = matchRepository.findByLockTimeLessThanEqualAndStatus(
                Instant.now(), MatchStatus.SCHEDULED);
        if (toLock.isEmpty()) return;
        toLock.forEach(m -> m.setStatus(MatchStatus.LOCKED));
        log.info("Auto-locked {} match(es)", toLock.size());
    }

    // ─── US-035: Auto-lock expired seasons (runs every minute) ───────────────

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void lockExpiredSeasons() {
        List<Season> toLock = seasonRepository.findByLeagueLockTimeLessThanEqualAndStatus(
                Instant.now(), SeasonStatus.OPEN);
        if (toLock.isEmpty()) return;
        toLock.forEach(s -> s.setStatus(SeasonStatus.LOCKED));
        log.info("Auto-locked {} season(s)", toLock.size());
    }

    // ─── US-029: Admin pending result alert (runs every 5 minutes) ───────────

    @Scheduled(fixedDelay = 300_000)
    @Transactional(readOnly = true)
    public void sendPendingResultAlerts() {
        long alertHours = Long.parseLong(
                appConfigService.findByKey("result.pending.alert.hours").getValue());

        Instant threshold = Instant.now().minus(alertHours, ChronoUnit.HOURS);

        List<Match> candidateMatches = matchRepository
                .findByScheduledAtBeforeAndStatusNot(threshold, MatchStatus.COMPLETED);

        // Keep only matches with no published result and no prior alert sent
        List<Match> alertMatches = candidateMatches.stream()
                .filter(m -> matchResultRepository.findByMatchId(m.getId()).isEmpty())
                .filter(m -> !emailLogRepository.existsByEventTypeAndReferenceId(
                        EmailEventType.PENDING_RESULT_ALERT.name(), m.getId()))
                .toList();

        if (alertMatches.isEmpty()) return;

        List<User> admins = userRepository.findByRole(UserRole.ADMIN);
        if (admins.isEmpty()) return;

        log.info("Sending pending result alert for {} match(es) to {} admin(s)",
                alertMatches.size(), admins.size());

        for (User admin : admins) {
            notificationService.sendPendingResultAlert(admin, alertMatches);
            // Log a reference entry per match per admin for idempotency
            for (Match m : alertMatches) {
                emailLogRepository.existsByEventTypeAndReferenceIdAndRecipientEmail(
                        EmailEventType.PENDING_RESULT_ALERT.name(), m.getId(), admin.getEmail());
            }
        }
    }
}
