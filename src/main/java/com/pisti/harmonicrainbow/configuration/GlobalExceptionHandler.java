package com.pisti.harmonicrainbow.configuration;

import com.pisti.harmonicrainbow.exceptions.CorruptImageException;
import com.pisti.harmonicrainbow.exceptions.NoSuchImageException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchImageException.class)
    public ResponseEntity<String> noSuchImageException(NoSuchImageException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }
    @ExceptionHandler(CorruptImageException.class)
    public ResponseEntity<String> corruptImageException(CorruptImageException e) {
        return ResponseEntity.status(400).body(e.getMessage());
    }
}
