package ru.fastdelivery.presentation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException e) {
        ApiError apiError = ApiError.badRequest(e.getMessage());
        return new ResponseEntity<>(apiError, apiError.httpStatus());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointer(NullPointerException e) {
        ApiError apiError = ApiError.badRequest("Internal configuration error: " + e.getMessage());
        return new ResponseEntity<>(apiError, apiError.httpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        ApiError apiError = ApiError.badRequest(message);
        return new ResponseEntity<>(apiError, apiError.httpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception e) {
        ApiError apiError = ApiError.badRequest("Unexpected error: " + e.getMessage());
        return new ResponseEntity<>(apiError, apiError.httpStatus());
    }
}

