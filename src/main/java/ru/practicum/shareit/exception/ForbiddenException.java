package ru.practicum.shareit.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ForbiddenException extends RuntimeException {
    long id;
    long itemId;
    String action;

    public ForbiddenException(final long id, final long itemId, String action) {
        super("User " + id + " cannot " + action + " item " + itemId + ".");
        this.id = id;
        this.itemId = itemId;
        this.action = action;
    }
}
