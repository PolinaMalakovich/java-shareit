package ru.practicum.shareit.dto.item;

import lombok.Value;
import ru.practicum.shareit.dto.booking.BookingDtoBookerId;

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
