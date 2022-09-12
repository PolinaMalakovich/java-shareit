package ru.practicum.shareit.request.dto;

import lombok.Value;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Value
public class ItemRequestDto {
    Long id;
    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    String description;
    UserDto requester;
    LocalDateTime created;
}
