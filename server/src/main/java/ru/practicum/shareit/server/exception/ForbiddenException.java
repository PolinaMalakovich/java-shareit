package ru.practicum.shareit.server.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ForbiddenException extends RuntimeException {
    long userId;
    long entityId;
    String entity;
    String action;

    public ForbiddenException(final long userId, final long entityId, final String entity, String action) {
        super("User " + userId + " cannot " + action + " " + entity + " " + entityId + ".");
        this.userId = userId;
        this.entityId = entityId;
        this.entity = entity;
        this.action = action;
    }
}
