package com.example.practicejava.appconfig;

import com.example.practicejava.appconfig.dto.AppConfigResponse;
import com.example.practicejava.appconfig.dto.UpdateAppConfigRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/config")
public class AppConfigController {

    private final AppConfigService appConfigService;

    public AppConfigController(AppConfigService appConfigService) {
        this.appConfigService = appConfigService;
    }

    @GetMapping
    public List<AppConfigResponse> list() {
        return appConfigService.findAll().stream().map(AppConfigResponse::from).toList();
    }

    @PutMapping("/{key}")
    public AppConfigResponse update(@PathVariable String key,
                                     @Valid @RequestBody UpdateAppConfigRequest request) {
        return AppConfigResponse.from(appConfigService.update(key, request));
    }
}
