package com.example.practicejava.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public UUID getId() { return id; }

    public Instant getCreatedAt() { return createdAt; }

    public UUID getCreatedBy() { return createdBy; }

    public Instant getUpdatedAt() { return updatedAt; }

    public UUID getUpdatedBy() { return updatedBy; }

    public Instant getDeletedAt() { return deletedAt; }

    public UUID getDeletedBy() { return deletedBy; }

    public boolean isDeleted() { return isDeleted; }

    public void softDelete(UUID deletedByUser) {
        this.isDeleted = true;
        this.deletedAt = Instant.now();
        this.deletedBy = deletedByUser;
    }
}
