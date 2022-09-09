package ru.practicum.shareit.request.model;

import lombok.Value;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Value
public class ItemRequest {
    Long id;
    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    String description;
    User requester;
    LocalDateTime created;
}
