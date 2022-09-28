package ru.practicum.shareit.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ItemUnavailableException extends RuntimeException {
    long itemId;
    public ItemUnavailableException(final long itemId) {
        super("Item " + itemId + " is unavailable.");
        this.itemId = itemId;
    }
}
