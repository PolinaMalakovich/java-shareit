package ru.practicum.shareit.request.model;

import lombok.Value;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Value
public class ItemRequest {
    Long id;
    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    String description;
    User requester;
    LocalDateTime created;

    public ItemRequest withId(Long id) {
        return Objects.equals(this.id, id) ? this : new ItemRequest(id, this.description, this.requester, this.created);
    }

    public ItemRequest withDescription(
        @Size(max = 200, message = "Description cannot be longer than 200 characters") String description) {
        return Objects.equals(this.description, description) ? this :
            new ItemRequest(this.id, description, this.requester, this.created);
    }

    public ItemRequest withRequester(User requester) {
        return Objects.equals(this.requester, requester) ? this :
            new ItemRequest(this.id, this.description, requester, this.created);
    }

    public ItemRequest withCreated(LocalDateTime created) {
        return Objects.equals(this.created, created) ? this :
            new ItemRequest(this.id, this.description, this.requester, created);
    }
}
