package com.example.practicejava.common;

public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final String path;

    private ApiResponse(boolean success, String message, T data, String path) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.path = path;
    }

    public static <T> ApiResponse<T> ok(T data, String path) {
        return new ApiResponse<>(true, "", data, path);
    }

    public static <T> ApiResponse<T> ok(String message, T data, String path) {
        return new ApiResponse<>(true, message, data, path);
    }

    public static ApiResponse<Void> deleted(String path) {
        return new ApiResponse<>(true, "Deleted successfully", null, path);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public String getPath() { return path; }
}
