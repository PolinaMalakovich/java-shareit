package ru.practicum.shareit.user.model;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Value
public class User {
    Long id;
    @NotBlank(message = "Name cannot be blank")
    String name;
    @Email(message = "Email should be valid")
    String email;

    public User withId(Long id) {
        return Objects.equals(this.id, id) ? this : new User(id, this.name, this.email);
    }

    public User withName(@NotBlank(message = "Name cannot be blank") String name) {
        return Objects.equals(this.name, name) ? this : new User(this.id, name, this.email);
    }

    public User withEmail(@Email(message = "Email should be valid") String email) {
        return Objects.equals(this.email, email) ? this : new User(this.id, this.name, email);
    }
}
