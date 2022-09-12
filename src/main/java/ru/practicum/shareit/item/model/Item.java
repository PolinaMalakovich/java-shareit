package ru.practicum.shareit.item.model;

import lombok.Value;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Size;
import java.util.Objects;

@Value
public class Item {
    Long id;
    String name;
    String description;
    boolean available;
    User owner;
    ItemRequest request;

    public boolean isAvailable() {
        return available;
    }

    public Item withId(Long id) {
        return Objects.equals(this.id, id) ? this :
            new Item(id, this.name, this.description, this.available, this.owner, this.request);
    }

    public Item withName(String name) {
        return Objects.equals(this.name, name) ? this :
            new Item(this.id, name, this.description, this.available, this.owner, this.request);
    }

    public Item withDescription(
        @Size(max = 200, message = "Description cannot be longer than 200 characters") String description) {
        return Objects.equals(this.description, description) ? this :
            new Item(this.id, this.name, description, this.available, this.owner, this.request);
    }

    public Item withAvailable(boolean available) {
        return Objects.equals(this.available, available) ? this :
            new Item(this.id, this.name, this.description, available, this.owner, this.request);
    }

    public Item withOwner(User owner) {
        return Objects.equals(this.owner, owner) ? this :
            new Item(this.id, this.name, this.description, this.available, owner, this.request);
    }

    public Item withRequest(ItemRequest request) {
        return Objects.equals(this.request, request) ? this :
            new Item(this.id, this.name, this.description, this.available, this.owner, request);
    }
}
