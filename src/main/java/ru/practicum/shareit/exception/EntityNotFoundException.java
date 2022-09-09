package ru.practicum.shareit.exception;

public class EntityNotFoundException extends RuntimeException {
    String entity;
    long id;

    public EntityNotFoundException(final String entity, final long id) {
        super(entity + " with id " + id + " does not exist.");
        this.entity = entity;
        this.id = id;
    }
}
