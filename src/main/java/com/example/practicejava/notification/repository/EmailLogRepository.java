package com.example.practicejava.notification.repository;

import com.example.practicejava.notification.EmailLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface EmailLogRepository extends JpaRepository<EmailLog, UUID>,
        JpaSpecificationExecutor<EmailLog> {

    Page<EmailLog> findAll(Pageable pageable);

    boolean existsByEventTypeAndReferenceIdAndRecipientEmail(String eventType, UUID referenceId, String recipientEmail);

    boolean existsByEventTypeAndReferenceId(String eventType, UUID referenceId);
}
