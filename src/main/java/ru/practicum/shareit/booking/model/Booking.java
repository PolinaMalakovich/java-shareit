package ru.practicum.shareit.booking.model;

import lombok.Value;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class Booking {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    @NotNull(message = "Item cannot be null")
    Item item;
    @NotNull(message = "Booker cannot be null")
    User booker;
    @NotNull(message = "Status cannot be null")
    Status status;
}
