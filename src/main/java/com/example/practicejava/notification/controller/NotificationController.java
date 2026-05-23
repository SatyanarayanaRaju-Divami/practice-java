package com.example.practicejava.notification.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.notification.EmailStatus;
import com.example.practicejava.notification.dto.BulkNotificationRequest;
import com.example.practicejava.notification.dto.EmailLogResponse;
import com.example.practicejava.notification.service.EmailLogService;
import com.example.practicejava.notification.service.NotificationService;
import com.example.practicejava.user.User;
import com.example.practicejava.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@Tag(name = "Notifications", description = "Bulk notifications and email log (admin only)")
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final EmailLogService emailLogService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationController(EmailLogService emailLogService,
                                   NotificationService notificationService,
                                   UserRepository userRepository) {
        this.emailLogService = emailLogService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    // ─── US-032: Admin bulk notification ─────────────────────────────────────

    @Operation(summary = "Send bulk notification to users (admin only)")
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<String>> bulk(@Valid @RequestBody BulkNotificationRequest request,
                                                     HttpServletRequest req) {
        List<User> recipients;
        if (request.userIds() == null || request.userIds().isEmpty()) {
            recipients = userRepository.findByIsActiveTrue();
        } else {
            recipients = userRepository.findAllById(request.userIds());
        }

        notificationService.sendBulk(recipients, request.eventType(), request.subject(), request.body());

        return ResponseEntity.ok(ApiResponse.ok(
                "Bulk notification queued for " + recipients.size() + " recipient(s)",
                null, req.getRequestURI()));
    }

    // ─── US-033: Admin email log with filters ─────────────────────────────────

    @Operation(summary = "List email logs with filters (admin only)")
    @GetMapping("/emails")
    public ResponseEntity<ApiResponse<Page<EmailLogResponse>>> listEmails(
            @RequestParam(required = false) String recipientEmail,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) EmailStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PageableDefault(size = 20, sort = "sentAt") Pageable pageable,
            HttpServletRequest req) {

        Page<EmailLogResponse> page = emailLogService
                .findFiltered(recipientEmail, eventType, status, from, to, pageable)
                .map(EmailLogResponse::from);

        return ResponseEntity.ok(ApiResponse.ok(page, req.getRequestURI()));
    }
}
