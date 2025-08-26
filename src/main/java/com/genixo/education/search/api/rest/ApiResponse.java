package com.genixo.education.search.api.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private LocalDateTime timestamp;
    private String path;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, null, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return new ApiResponse<>(false, message, null, errors, LocalDateTime.now(), null);
    }
}
