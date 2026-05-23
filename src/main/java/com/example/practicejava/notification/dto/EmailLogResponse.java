package com.example.practicejava.notification.dto;

import com.example.practicejava.notification.EmailLog;
import com.example.practicejava.notification.EmailStatus;

import java.time.Instant;
import java.util.UUID;

public record EmailLogResponse(
        UUID id,
        String recipientEmail,
        String eventType,
        String subject,
        EmailStatus status,
        Instant sentAt,
        String errorMessage
) {
    public static EmailLogResponse from(EmailLog log) {
        return new EmailLogResponse(
                log.getId(),
                log.getRecipientEmail(),
                log.getEventType(),
                log.getSubject(),
                log.getStatus(),
                log.getSentAt(),
                log.getErrorMessage()
        );
    }
}
