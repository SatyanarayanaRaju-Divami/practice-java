package com.example.practicejava.notification.service;

import com.example.practicejava.notification.EmailLog;
import com.example.practicejava.notification.EmailStatus;
import com.example.practicejava.notification.repository.EmailLogRepository;
import com.example.practicejava.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final int SUMMARY_MAX_LEN = 500;

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    @Value("${spring.mail.from:noreply@familyleague.app}")
    private String fromAddress;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    public EmailService(JavaMailSender mailSender, EmailLogRepository emailLogRepository) {
        this.mailSender = mailSender;
        this.emailLogRepository = emailLogRepository;
    }

    /**
     * Sends an email to a known user, logs the attempt to email_log.
     * referenceId is stored for idempotency (e.g. match ID).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendAndLog(User recipient, String eventType, String subject,
                           String body, UUID referenceId) {
        EmailLog entry = new EmailLog(recipient, recipient.getEmail(), eventType, subject);
        entry.setBodySummary(body.length() > SUMMARY_MAX_LEN ? body.substring(0, SUMMARY_MAX_LEN) : body);
        entry.setReferenceId(referenceId);
        emailLogRepository.save(entry);

        send(entry, subject, body, recipient.getEmail());
    }

    /**
     * Sends to an arbitrary email address (e.g. admin-only alert with multiple match refs).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendAndLog(User recipient, String eventType, String subject,
                           String body) {
        sendAndLog(recipient, eventType, subject, body, null);
    }

    private void send(EmailLog entry, String subject, String body, String to) {
        if (mailUsername == null || mailUsername.isBlank()) {
            log.info("[EMAIL SIMULATED] to={} subject=\"{}\" body={}", to, subject,
                    body.replace("\n", " "));
            entry.setStatus(EmailStatus.SENT);
            entry.setSentAt(Instant.now());
            return;
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            entry.setStatus(EmailStatus.SENT);
            entry.setSentAt(Instant.now());
        } catch (MailException e) {
            log.error("[EMAIL FAILED] to={} subject=\"{}\" error={}", to, subject, e.getMessage());
            entry.setStatus(EmailStatus.FAILED);
            entry.setErrorMessage(e.getMessage());
        }
    }
}
