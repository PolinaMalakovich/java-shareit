package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@Value
public class UserDto {
    Long id;
    @NotBlank(message = "Name cannot be blank")
    String name;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    String email;

    public UserDto withId(Long id) {
        return Objects.equals(this.id, id) ? this : new UserDto(id, this.name, this.email);
    }

    public UserDto withName(@NotBlank(message = "Name cannot be blank") String name) {
        return Objects.equals(this.name, name) ? this : new UserDto(this.id, name, this.email);
    }

    public UserDto withEmail(
        @NotEmpty(message = "Email cannot be empty") @Email(message = "Email should be valid") String email) {
        return Objects.equals(this.email, email) ? this : new UserDto(this.id, this.name, email);
    }
}
