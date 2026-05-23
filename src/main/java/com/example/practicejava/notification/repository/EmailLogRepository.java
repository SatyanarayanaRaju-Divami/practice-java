package com.example.practicejava.notification.repository;

import com.example.practicejava.notification.EmailLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> {
    Page<EmailLog> findAll(Pageable pageable);
}
