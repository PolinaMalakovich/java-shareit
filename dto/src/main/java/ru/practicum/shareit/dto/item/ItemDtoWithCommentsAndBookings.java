package ru.practicum.shareit.dto.item;

import lombok.Value;
import ru.practicum.shareit.dto.booking.BookingDtoBookerId;
import ru.practicum.shareit.dto.user.UserDto;

import java.util.List;

@Value
public class ItemDtoWithCommentsAndBookings {
    Long id;
    String name;
    String description;
    Boolean available;
    UserDto owner;
    Long request;
    List<CommentDto> comments;
    BookingDtoBookerId lastBooking;
    BookingDtoBookerId nextBooking;

    public boolean isAvailable() {
        return available;
    }
}
