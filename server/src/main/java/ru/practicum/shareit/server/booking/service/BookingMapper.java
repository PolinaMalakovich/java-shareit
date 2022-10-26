package ru.practicum.shareit.server.booking.service;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.dto.booking.BookingDto;
import ru.practicum.shareit.dto.booking.BookingDtoBookerId;
import ru.practicum.shareit.dto.booking.NewBookingDto;
import ru.practicum.shareit.dto.booking.Status;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemMapper;
import ru.practicum.shareit.server.user.model.User;

@UtilityClass
public class BookingMapper {
    public static BookingDto toBookingDto(final Booking booking) {
        BookingDto.Booker booker = new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName());
        BookingDto.BookedItem item = new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName());

        return new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            item,
            booker,
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
