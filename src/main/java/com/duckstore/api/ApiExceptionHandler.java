package com.duckstore.api;

import com.duckstore.duck.NotFoundException;
import org.springframework.http.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class) @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String,String> notFound(NotFoundException e) { return Map.of("error", e.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String,String> invalid(MethodArgumentNotValidException e) { return Map.of("error", e.getBindingResult().getFieldErrors().getFirst().getField() + " is invalid."); }
    @ExceptionHandler(DataIntegrityViolationException.class) @ResponseStatus(HttpStatus.CONFLICT)
    Map<String,String> conflict(DataIntegrityViolationException e) { return Map.of("error", "This edit would duplicate an active duck identity."); }
}
