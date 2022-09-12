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
    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    String description;
    @NotNull
    Boolean available;
    UserDto owner;
    Long request;

    public boolean isAvailable() {
        return available;
    }

    public ItemDto withId(Long id) {
        return Objects.equals(this.id, id) ? this :
            new ItemDto(id, this.name, this.description, this.available, this.owner, this.request);
    }

    public ItemDto withName(@NotBlank(message = "Name cannot be blank") String name) {
        return Objects.equals(this.name, name) ? this :
            new ItemDto(this.id, name, this.description, this.available, this.owner, this.request);
    }

    public ItemDto withDescription(
        @NotNull @Size(max = 200, message = "Description cannot be longer than 200 characters") String description) {
        return Objects.equals(this.description, description) ? this :
            new ItemDto(this.id, this.name, description, this.available, this.owner, this.request);
    }

    public ItemDto withAvailable(@NotNull Boolean available) {
        return Objects.equals(this.available, available) ? this :
            new ItemDto(this.id, this.name, this.description, available, this.owner, this.request);
    }

    public ItemDto withOwner(UserDto owner) {
        return Objects.equals(this.owner, owner) ? this :
            new ItemDto(this.id, this.name, this.description, this.available, owner, this.request);
    }

    public ItemDto withRequest(long request) {
        return Objects.equals(this.request, request) ? this :
            new ItemDto(this.id, this.name, this.description, this.available, this.owner, request);
    }
}
