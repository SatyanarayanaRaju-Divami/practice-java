package com.example.practicejava.auth.dto;

import com.example.practicejava.common.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateAdminRequest(
        @Email @NotBlank String email,
        @ValidPassword String password,
        @NotBlank String displayName
) {}
