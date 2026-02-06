package com.clinic.medical_clinic_scheduler.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "Standard error response model")
public record ApiError(
        @Schema(description = "Timestamp of the error", example = "2026-02-06 12:00:00")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,

        @Schema(description = "HTTP Status Code", example = "400")
        int status,

        @Schema(description = "Error type", example = "Bad Request")
        String error,

        @Schema(description = "Detailed error message", example = "Validation failed")
        String message,

        @Schema(description = "List of validation errors (optional)")
        List<String> details
) {
    public ApiError(HttpStatus status, String message, List<String> details) {
        this(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, details);
    }
}