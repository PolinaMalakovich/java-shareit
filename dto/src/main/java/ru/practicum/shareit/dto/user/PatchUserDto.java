package ru.practicum.shareit.dto.user;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Value
public class PatchUserDto {
    @Pattern(regexp = "\\S+")
    String name;
    @Email(message = "Email should be valid")
    String email;
}
