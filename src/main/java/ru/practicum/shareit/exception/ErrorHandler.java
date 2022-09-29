package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.*;
import static ru.practicum.shareit.util.StringUtils.suffix;

@Slf4j
@RestControllerAdvice
public final class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleValidationException(final ValidationException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(
        final ConstraintViolationException e
    ) {
        log.error(e.getMessage());
        final Map<String, String> errorDetails = e.getConstraintViolations()
            .stream()
            .collect(toMap(
                violation -> suffix(violation.getPropertyPath().toString(), '.'),
                ConstraintViolation::getMessage
            ));

        return new ResponseEntity<>(errorDetails, BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleItemUnavailableException(final ItemUnavailableException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
        final MethodArgumentTypeMismatchException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(Map.of("error", "Unknown state: UNSUPPORTED_STATUS"), BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleIllegalArgumentException(final IllegalArgumentException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleUnauthorizedCommentException(final UnauthorizedCommentException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleDuplicateValueException(final DuplicateValueException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleForbiddenException(final ForbiddenException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleEntityNotFoundException(final EntityNotFoundException e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleThrowable(final Throwable e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
    }
}

