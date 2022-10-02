package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDtoBookerId;

import java.util.List;

@Value
public class ListItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    List<CommentDto> comments;
    BookingDtoBookerId lastBooking;
    BookingDtoBookerId nextBooking;
}
