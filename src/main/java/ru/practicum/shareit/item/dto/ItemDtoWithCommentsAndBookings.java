package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDtoBookerId;
import ru.practicum.shareit.user.dto.UserDto;

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
