package ru.practicum.shareit.dto.item;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
public class PatchItemDto {
    @NotBlank(message = "Name cannot be blank")
    String name;
    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    String description;
    Boolean available;
}
