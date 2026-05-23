package com.example.practicejava.common;

import java.util.List;

public class ApiErrorResponse {

    private final boolean success = false;
    private final String message;
    private final List<FieldError> errors;
    private final String path;

    private ApiErrorResponse(String message, List<FieldError> errors, String path) {
        this.message = message;
        this.errors = errors;
        this.path = path;
    }

    public static ApiErrorResponse of(String message, String path) {
        return new ApiErrorResponse(message, List.of(), path);
    }

    public static ApiErrorResponse of(String message, List<FieldError> errors, String path) {
        return new ApiErrorResponse(message, errors, path);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<FieldError> getErrors() { return errors; }
    public String getPath() { return path; }

    public record FieldError(String field, String error) {}
}
