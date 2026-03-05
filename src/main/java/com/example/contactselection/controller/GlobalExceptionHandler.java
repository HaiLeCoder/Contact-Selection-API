package com.example.contactselection.controller;

import com.example.contactselection.dto.common.ApiResponse;
import com.example.contactselection.exception.NoResultException;
import com.example.contactselection.exception.SelectionLimitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler.
 *
 * Centralises error handling for all controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Bean Validation errors → 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationError(
            MethodArgumentNotValidException ex) {

        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("[Validation] {}", details);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("Validation thất bại: " + details, "MSG_VALIDATION"));
    }

    /**
     * No result → 404 Not Found
     */
    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResult(NoResultException ex) {
        log.info("[NoResult] {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), "MSG_NO_RESULT"));
    }

    /**
     * Selection limit violation → 422 Unprocessable Entity
     */
    @ExceptionHandler(SelectionLimitException.class)
    public ResponseEntity<ApiResponse<Void>> handleSelectionLimit(SelectionLimitException ex) {
        log.warn("[SelectionLimit] {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.error(ex.getMessage(), "MSG_SELECT_ONE"));
    }

    /**
     * Unexpected errors → 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {
        log.error("[Unexpected Error]", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Lỗi hệ thống, vui lòng thử lại sau.", "MSG_SERVER_ERROR"));
    }
}
