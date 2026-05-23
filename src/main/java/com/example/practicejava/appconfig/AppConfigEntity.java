package com.example.practicejava.appconfig;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_config")
public class AppConfigEntity {

    @Id
    private String key;

    @Column(nullable = false)
    private String value;

    @Column(columnDefinition = "TEXT")
    private String description;

    protected AppConfigEntity() {}

    public AppConfigEntity(String key, String value, String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }

    public String getKey() { return key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
