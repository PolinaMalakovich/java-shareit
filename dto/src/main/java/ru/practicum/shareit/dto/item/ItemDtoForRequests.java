package ru.practicum.shareit.dto.item;

import lombok.Value;

@Value
public class ItemDtoForRequests {
    long id;
    String name;
    String description;
    boolean available;
    long requestId;
}
