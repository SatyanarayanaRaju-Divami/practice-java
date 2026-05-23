package com.example.practicejava.appconfig.repository;

import com.example.practicejava.appconfig.AppConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppConfigRepository extends JpaRepository<AppConfigEntity, String> {
}
