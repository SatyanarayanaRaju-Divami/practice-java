package com.example.practicejava.notification.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.notification.dto.BulkNotificationRequest;
import com.example.practicejava.notification.dto.EmailLogResponse;
import com.example.practicejava.notification.service.EmailLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<ApiResponse<String>> bulk(@Valid @RequestBody BulkNotificationRequest request,
                                                     HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Bulk notification not yet implemented", null, req.getRequestURI()));
    }

    @GetMapping("/emails")
    public ResponseEntity<ApiResponse<Page<EmailLogResponse>>> listEmails(Pageable pageable,
                                                                            HttpServletRequest req) {
        Page<EmailLogResponse> page = emailLogService.findAll(pageable).map(EmailLogResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(page, req.getRequestURI()));
    }
}
