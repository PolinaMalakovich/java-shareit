package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;

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
