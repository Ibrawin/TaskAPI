package com.ibrawin.taskapi.exceptions;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String message,
        List<String> fieldErrors
) {

    public ApiError(int status, String message, List<String> fieldErrors) {
        this(LocalDateTime.now(), status, message, fieldErrors);
    }

    public ApiError(int status, String message) {
        this(status, message, List.of());
    }

}
