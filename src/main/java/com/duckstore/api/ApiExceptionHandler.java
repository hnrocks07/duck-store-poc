package com.duckstore.api;

import com.duckstore.duck.DuplicateDuckException;
import com.duckstore.duck.NotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
  private static final org.slf4j.Logger LOG =
      org.slf4j.LoggerFactory.getLogger(ApiExceptionHandler.class);

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  Map<String, String> notFound(NotFoundException exception) {
    return Map.of("error", exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  Map<String, Object> invalid(MethodArgumentNotValidException exception) {
    Map<String, String> fields = new LinkedHashMap<>();
    exception
        .getBindingResult()
        .getFieldErrors()
        .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
    return Map.of("error", "Validation failed", "fields", fields);
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    MethodArgumentTypeMismatchException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  Map<String, String> unreadable(Exception exception) {
    Throwable cause = exception;
    while (cause != null) {
      if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException invalid
          && invalid.getTargetType().isEnum()) {
        String field =
            invalid.getPath().isEmpty() ? "value" : invalid.getPath().getLast().getFieldName();
        String allowed =
            java.util.Arrays.stream(invalid.getTargetType().getEnumConstants())
                .map(Object::toString)
                .collect(java.util.stream.Collectors.joining(", "));
        return Map.of("error", field + " must be one of " + allowed);
      }
      cause = cause.getCause();
    }
    return Map.of("error", "Invalid request payload.");
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  Map<String, String> conflict(Exception exception) {
    return Map.of("error", "Request conflicts with an existing duck.");
  }

  @ExceptionHandler(DuplicateDuckException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  Map<String, String> duplicate(DuplicateDuckException exception) {
    return Map.of("error", exception.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  Map<String, String> unexpected(Exception exception) {
    LOG.error("Unexpected request failure", exception);
    return Map.of("error", "Unexpected error");
  }
}
