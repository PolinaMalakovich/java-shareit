package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoBookerId;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toBookingDto(final Booking booking) {
        return new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            ItemMapper.toItemDto(booking.getItem()),
            UserMapper.toUserDto(booking.getBooker()),
            booking.getStatus());
    }

    public static BookingDtoBookerId toBookingDtoBookerId(final Booking booking) {
        return new BookingDtoBookerId(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            ItemMapper.toItemDto(booking.getItem()),
            booking.getBooker().getId(),
            booking.getStatus()
        );
    }

    public static Booking toBooking(final NewBookingDto newBookingDto,
                                    final Item item,
                                    final User booker) {
        return new Booking(
            null,
            newBookingDto.getStart(),
            newBookingDto.getEnd(),
            item,
            booker,
            Status.WAITING
        );
    }
}
