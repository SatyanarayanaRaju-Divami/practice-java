package com.example.practicejava.notification.service;

import com.example.practicejava.notification.EmailLog;
import com.example.practicejava.notification.repository.EmailLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmailLogService {

    private final EmailLogRepository emailLogRepository;

    public EmailLogService(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    @Transactional(readOnly = true)
    public Page<EmailLog> findAll(Pageable pageable) {
        return emailLogRepository.findAll(pageable);
    }
}
