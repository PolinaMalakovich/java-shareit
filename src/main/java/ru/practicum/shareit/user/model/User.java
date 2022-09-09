package ru.practicum.shareit.user.model;

import lombok.Value;
import lombok.With;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
@With
public class User {
    Long id;
    @NotBlank(message = "Name cannot be blank")
    String name;
    @Email(message = "Email should be valid")
    String email;
}
