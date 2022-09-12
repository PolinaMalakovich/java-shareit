package ru.practicum.shareit.user.model;

import lombok.Value;

import java.util.Objects;

@Value
public class User {
    Long id;
    String name;
    String email;

    public User withId(Long id) {
        return Objects.equals(this.id, id) ? this : new User(id, this.name, this.email);
    }

    public User withName(String name) {
        return Objects.equals(this.name, name) ? this : new User(this.id, name, this.email);
    }

    public User withEmail(String email) {
        return Objects.equals(this.email, email) ? this : new User(this.id, this.name, email);
    }
}
