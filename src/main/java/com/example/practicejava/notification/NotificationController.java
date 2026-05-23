package com.example.practicejava.notification;

import com.example.practicejava.notification.dto.BulkNotificationRequest;
import com.example.practicejava.notification.dto.EmailLogResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final EmailLogService emailLogService;

    public NotificationController(EmailLogService emailLogService) {
        this.emailLogService = emailLogService;
    }

    @PostMapping("/bulk")
    public ResponseEntity<String> bulk(@Valid @RequestBody BulkNotificationRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Bulk notification not yet implemented");
    }

    @GetMapping("/emails")
    public Page<EmailLogResponse> listEmails(Pageable pageable) {
        return emailLogService.findAll(pageable).map(EmailLogResponse::from);
    }
}
