package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDtoBookerId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Value
public class ListItemDto {
    Long id;
    @NotBlank(message = "Name cannot be blank")
    String name;
    @Size(max = 256, message = "Description cannot be longer than 256 characters")
    String description;
    @NotNull
    Boolean available;
    List<CommentDto> comments;
    BookingDtoBookerId lastBooking;
    BookingDtoBookerId nextBooking;
}
