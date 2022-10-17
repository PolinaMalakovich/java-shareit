package ru.practicum.shareit.request.dto;

import lombok.Value;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Value
public class ItemRequestDto {
    Long id;
    @Size(max = 256, message = "Description cannot be longer than 256 characters")
    @NotBlank
    String description;
    UserDto requester;
    LocalDateTime created;

    public ItemRequestDto withRequester(UserDto requester) {
        return Objects.equals(this.requester, requester) ? this :
            new ItemRequestDto(this.id, this.description, requester, this.created);
    }
}
