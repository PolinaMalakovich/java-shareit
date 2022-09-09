package ru.practicum.shareit.user.dto;

import lombok.Value;
import lombok.With;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Value
@With
public class UserDto {
    Long id;
    @NotBlank(message = "Name cannot be blank")
    String name;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    String email;
}