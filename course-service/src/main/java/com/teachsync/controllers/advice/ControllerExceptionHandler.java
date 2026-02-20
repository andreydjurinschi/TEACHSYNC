package com.teachsync.controllers.advice;

import com.teachsync.exceptions.ServiceUnavailableException;
import com.teachsync.interaction.feign.fallbacks.FallbackMessage;
import feign.FeignException;
import jakarta.persistence.ElementCollection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidError(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(
                error -> {
                    errors.put(error.getField(), error.getDefaultMessage());
                }
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchEntityException(NoSuchElementException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<String> handleFeignNotFoundError(FeignException.NotFound e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getLocalizedMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentError(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getLocalizedMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<FallbackMessage> handleServiceUnavailableException(ServiceUnavailableException e){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new FallbackMessage(
                        HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), "circuit breaker fall case", "course-service", "user-service"
                ));
    }

}
