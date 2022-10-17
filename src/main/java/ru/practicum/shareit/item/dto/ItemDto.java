package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Value
public class ItemDto {
    Long id;
    @NotBlank(message = "Name cannot be blank")
    String name;
    @NotNull
    @Size(max = 256, message = "Description cannot be longer than 256 characters")
    String description;
    @NotNull
    Boolean available;
    UserDto owner;
    Long requestId;

    public boolean isAvailable() {
        return available;
    }

    public ItemDto withOwner(UserDto owner) {
        return Objects.equals(this.owner, owner) ? this :
            new ItemDto(this.id, this.name, this.description, this.available, owner, this.requestId);
    }
}
