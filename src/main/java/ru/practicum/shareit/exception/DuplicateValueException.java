package ru.practicum.shareit.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class DuplicateValueException extends RuntimeException {
    String email;

    public DuplicateValueException(final String email) {
        super("A user with email " + email + " already exists.");
        this.email = email;
    }
}
