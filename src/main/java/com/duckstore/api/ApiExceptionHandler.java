package com.duckstore.api;

import com.duckstore.duck.NotFoundException;
import com.duckstore.duck.DuplicateDuckException;
import org.springframework.http.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class) @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String,String> notFound(NotFoundException e) { return Map.of("error", e.getMessage()); }
    @ExceptionHandler(MethodArgumentNotValidException.class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String,Object> invalid(MethodArgumentNotValidException e) {
        Map<String,String> fields=new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error->fields.put(error.getField(),error.getDefaultMessage()));
        return Map.of("error","Validation failed","fields",fields);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class) @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String,String> unreadable(HttpMessageNotReadableException e) { return Map.of("error", "Validation failed"); }
    @ExceptionHandler(DataIntegrityViolationException.class) @ResponseStatus(HttpStatus.CONFLICT)
    Map<String,String> conflict(DataIntegrityViolationException e) { return Map.of("error", "This edit would duplicate an active duck identity."); }
    @ExceptionHandler(DuplicateDuckException.class) @ResponseStatus(HttpStatus.CONFLICT)
    Map<String,String> duplicate(DuplicateDuckException e) { return Map.of("error", e.getMessage()); }
}
