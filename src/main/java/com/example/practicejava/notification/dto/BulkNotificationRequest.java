package com.example.practicejava.notification.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record BulkNotificationRequest(List<UUID> userIds, @NotBlank String eventType,
                                       @NotBlank String subject, @NotBlank String body) {
}
