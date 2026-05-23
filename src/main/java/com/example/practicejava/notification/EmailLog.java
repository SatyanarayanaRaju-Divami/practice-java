package com.example.practicejava.notification;

import com.example.practicejava.common.BaseEntity;
import com.example.practicejava.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_log")
public class EmailLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id")
    private User recipientUser;

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String subject;

    @Column(name = "body_summary", columnDefinition = "TEXT")
    private String bodySummary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailStatus status = EmailStatus.PENDING;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "reference_id")
    private UUID referenceId;

    protected EmailLog() {}

    public EmailLog(User recipientUser, String recipientEmail, String eventType, String subject) {
        this.recipientUser = recipientUser;
        this.recipientEmail = recipientEmail;
        this.eventType = eventType;
        this.subject = subject;
    }

    public User getRecipientUser() { return recipientUser; }

    public String getRecipientEmail() { return recipientEmail; }

    public String getEventType() { return eventType; }

    public String getSubject() { return subject; }

    public String getBodySummary() { return bodySummary; }
    public void setBodySummary(String bodySummary) { this.bodySummary = bodySummary; }

    public EmailStatus getStatus() { return status; }
    public void setStatus(EmailStatus status) { this.status = status; }

    public Instant getSentAt() { return sentAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public UUID getReferenceId() { return referenceId; }
    public void setReferenceId(UUID referenceId) { this.referenceId = referenceId; }
}
