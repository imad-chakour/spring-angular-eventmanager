package com.example.event_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        System.err.println("❌ Erreur de parsing JSON:");
        System.err.println("   Message: " + e.getMessage());
        System.err.println("   Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"));
        e.printStackTrace();
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid request format");
        error.put("message", "Erreur de parsing des données. Vérifiez le format des dates (yyyy-MM-ddTHH:mm:ss) et des enums.");
        if (e.getCause() != null) {
            error.put("details", e.getCause().getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        System.err.println("❌ Erreur de validation: " + e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Validation error");
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        System.err.println("❌ Erreur inattendue:");
        System.err.println("   Message: " + e.getMessage());
        System.err.println("   Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "N/A"));
        e.printStackTrace();
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error");
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
