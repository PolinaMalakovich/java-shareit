package ru.practicum.shareit.dto.user;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Value
public class UserDto {
    Long id;
    @NotBlank(message = "Name cannot be blank")
    String name;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    String email;
}
