package com.example.practicejava.user.controller;

import com.example.practicejava.common.ApiResponse;
import com.example.practicejava.user.dto.UpdateUserRequest;
import com.example.practicejava.user.dto.UserResponse;
import com.example.practicejava.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Users", description = "User profile management")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "List all users (admin only, paginated)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> list(
            @PageableDefault(size = 20, sort = "displayName") Pageable pageable,
            HttpServletRequest req) {
        Page<UserResponse> users = userService.findAll(pageable).map(UserResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(users, req.getRequestURI()));
    }

    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal UUID userId,
                                                            HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(userService.findMe(userId)), req.getRequestURI()));
    }

    @Operation(summary = "Update current user profile")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(@AuthenticationPrincipal UUID userId,
                                                               @Valid @RequestBody UpdateUserRequest request,
                                                               HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(userService.updateMe(userId, request)), req.getRequestURI()));
    }

    @Operation(summary = "Get a user by ID (admin only)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> get(@PathVariable UUID id, HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(userService.findById(id)), req.getRequestURI()));
    }

    @Operation(summary = "Update a user (admin only)")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable UUID id,
                                                             @Valid @RequestBody UpdateUserRequest request,
                                                             HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(userService.update(id, request)), req.getRequestURI()));
    }

    @Operation(summary = "Delete a user (admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id,
                                                     @AuthenticationPrincipal UUID deletedBy,
                                                     HttpServletRequest req) {
        userService.delete(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.deleted(req.getRequestURI()));
    }
}
