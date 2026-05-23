package com.example.practicejava.auth.controller;

import com.example.practicejava.auth.dto.AuthResponse;
import com.example.practicejava.auth.dto.CreateAdminRequest;
import com.example.practicejava.auth.dto.LoginRequest;
import com.example.practicejava.auth.dto.RegisterRequest;
import com.example.practicejava.auth.service.AuthService;
import com.example.practicejava.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Register, login, and token management")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request,
                                                               HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.register(request), req.getRequestURI()));
    }

    @Operation(summary = "Login and obtain a JWT token")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request,
                                                            HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request), req.getRequestURI()));
    }

    @Operation(summary = "Create an admin account (admin only)")
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<AuthResponse>> createAdmin(@Valid @RequestBody CreateAdminRequest request,
                                                                   HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.createAdmin(request), req.getRequestURI()));
    }

    @Operation(summary = "Refresh JWT token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestHeader("Authorization") String authHeader,
                                                              HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(authHeader), req.getRequestURI()));
    }
}
