package ru.practicum.shareit.item.model;

import lombok.Value;
import lombok.With;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@With
public class Item {
    Long id;
    @NotBlank(message = "Name cannot be blank")
    String name;
    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    String description;
    boolean available;
    User owner;
    ItemRequest request;

    public boolean isAvailable() {
        return available;
    }
}
