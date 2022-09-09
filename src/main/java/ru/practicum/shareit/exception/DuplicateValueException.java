package ru.practicum.shareit.exception;

public class DuplicateValueException extends RuntimeException {
    String email;

    public DuplicateValueException(final String email) {
        super("A user with email " + email + " already exists.");
        this.email = email;
    }
}
