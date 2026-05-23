package com.example.practicejava.notification.service;

import com.example.practicejava.notification.EmailLog;
import com.example.practicejava.notification.EmailStatus;
import com.example.practicejava.notification.repository.EmailLogRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    // ─── US-033: Filtered email log ───────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<EmailLog> findFiltered(String recipientEmail, String eventType,
                                       EmailStatus status, Instant from, Instant to,
                                       Pageable pageable) {
        Specification<EmailLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (recipientEmail != null && !recipientEmail.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("recipientEmail")),
                        "%" + recipientEmail.toLowerCase() + "%"));
            }
            if (eventType != null && !eventType.isBlank()) {
                predicates.add(cb.equal(root.get("eventType"), eventType));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("sentAt"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("sentAt"), to));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return emailLogRepository.findAll(spec, pageable);
    }
}
