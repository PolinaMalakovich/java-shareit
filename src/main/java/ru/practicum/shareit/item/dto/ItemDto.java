package ru.practicum.shareit.item.dto;

import lombok.Value;
import lombok.With;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@With
public class ItemDto {
    Long id;
    @NotBlank(message = "Name cannot be blank")
    String name;
    @NotNull
    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    String description;
    @NotNull
    Boolean available;
    UserDto owner;
    ItemRequestDto request;

    public boolean isAvailable() {
        return available;
    }
}
