package com.example.practicejava.user.dto;

import com.example.practicejava.user.User;
import com.example.practicejava.user.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, String displayName, String email, String avatarUrl, UserRole role, Instant createdAt) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
