package com.example.practicejava.appconfig.controller;

import com.example.practicejava.appconfig.dto.AppConfigResponse;
import com.example.practicejava.appconfig.dto.UpdateAppConfigRequest;
import com.example.practicejava.appconfig.service.AppConfigService;
import com.example.practicejava.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Configuration", description = "Admin-only application config key-value pairs")
@RestController
@RequestMapping("/api/v1/config")
public class AppConfigController {

    private final AppConfigService appConfigService;

    public AppConfigController(AppConfigService appConfigService) {
        this.appConfigService = appConfigService;
    }

    @Operation(summary = "List all configuration entries")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AppConfigResponse>>> list(HttpServletRequest req) {
        List<AppConfigResponse> configs = appConfigService.findAll().stream().map(AppConfigResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(configs, req.getRequestURI()));
    }

    @Operation(summary = "Update a configuration value by key")
    @PutMapping("/{key}")
    public ResponseEntity<ApiResponse<AppConfigResponse>> update(@PathVariable String key,
                                                                   @Valid @RequestBody UpdateAppConfigRequest request,
                                                                   HttpServletRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(AppConfigResponse.from(appConfigService.update(key, request)), req.getRequestURI()));
    }
}
