package com.clinic.medical_clinic_scheduler.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            PatientAlreadyExistsException.class,
            DoctorAlreadyExistsException.class,
            IllegalStateException.class,
            ObjectOptimisticLockingFailureException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    @ApiResponse(responseCode = "409", description = "Conflict: Resource already exists or concurrent modification",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ApiError> handleConflictExceptions(RuntimeException ex) {
        ApiError apiError = new ApiError(HttpStatus.CONFLICT, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(responseCode = "404", description = "Resource not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ApiError> handleEntityNotFound(jakarta.persistence.EntityNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Validation failed", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ApiError> handleGeneralException(Exception ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", List.of(ex.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
}