package com.example.practicejava.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank String displayName,
        String avatarUrl
) {
}
