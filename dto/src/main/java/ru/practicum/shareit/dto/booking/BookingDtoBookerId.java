package ru.practicum.shareit.dto.booking;

import lombok.Value;
import ru.practicum.shareit.dto.item.ItemDto;

import java.time.LocalDateTime;

@Value
public class BookingDtoBookerId {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemDto item;
    long bookerId;
    Status status;
}
