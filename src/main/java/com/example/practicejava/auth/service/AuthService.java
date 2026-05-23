package com.example.practicejava.auth.service;

import com.example.practicejava.auth.EmailAlreadyExistsException;
import com.example.practicejava.auth.JwtService;
import com.example.practicejava.auth.dto.AuthResponse;
import com.example.practicejava.auth.dto.CreateAdminRequest;
import com.example.practicejava.auth.dto.LoginRequest;
import com.example.practicejava.auth.dto.RegisterRequest;
import com.example.practicejava.user.User;
import com.example.practicejava.user.UserRole;
import com.example.practicejava.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException(request.email());
        }
        User user = new User(request.displayName(), request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        String token = jwtService.generateToken(user.getId(), "ROLE_" + user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getRole().name());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getId(), "ROLE_" + user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getRole().name());
    }

    public AuthResponse createAdmin(CreateAdminRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException(request.email());
        }
        User admin = new User(request.displayName(), request.email());
        admin.setPasswordHash(passwordEncoder.encode(request.password()));
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);
        String token = jwtService.generateToken(admin.getId(), "ROLE_" + admin.getRole().name());
        return new AuthResponse(token, admin.getId(), admin.getRole().name());
    }

    @Transactional(readOnly = true)
    public AuthResponse refresh(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new BadCredentialsException("Missing or invalid Authorization header");
        }
        String token = bearerToken.substring(7);
        try {
            UUID userId = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);
            String newToken = jwtService.generateToken(userId, role);
            return new AuthResponse(newToken, userId, role.replace("ROLE_", ""));
        } catch (JwtException e) {
            throw new BadCredentialsException("Token is invalid or expired");
        }
    }
}
