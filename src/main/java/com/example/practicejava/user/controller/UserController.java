package com.example.practicejava.user.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.user.User;
import com.example.practicejava.user.dto.CreateUserRequest;
import com.example.practicejava.user.dto.UpdateUserRequest;
import com.example.practicejava.user.dto.UserResponse;
import com.example.practicejava.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> list(HttpServletRequest req) {
        List<UserResponse> users = userService.findAll().stream().map(UserResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(users, req.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> get(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(userService.findById(id)), req.getRequestURI()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest request,
                                                             HttpServletRequest req) {
        User created = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User created successfully", UserResponse.from(created), req.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable UUID id,
                                                             @Valid @RequestBody UpdateUserRequest request,
                                                             HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(userService.update(id, request)), req.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id, HttpServletRequest req) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }
}
