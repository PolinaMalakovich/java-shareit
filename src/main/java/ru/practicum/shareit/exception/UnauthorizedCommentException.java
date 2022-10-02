package ru.practicum.shareit.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class UnauthorizedCommentException extends RuntimeException {
    long userId;
    long itemId;

    public UnauthorizedCommentException(final long userId, final long itemId) {
        super("User " + userId + " cannot comment on item " + itemId + ".");
        this.userId = userId;
        this.itemId = itemId;
    }
}
