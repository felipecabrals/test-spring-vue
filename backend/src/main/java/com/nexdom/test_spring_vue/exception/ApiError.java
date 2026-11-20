package com.nexdom.test_spring_vue.exception;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;

public record ApiError(Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> detalhes) {
    public static ApiError of(HttpStatus status, String message, String path, List<String> detalhes) {
        return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(),
                message, path, detalhes);
    }

    public static ApiError of(HttpStatus status, String message, String path) {
        return of(status, message, path, List.of());
    }
}
