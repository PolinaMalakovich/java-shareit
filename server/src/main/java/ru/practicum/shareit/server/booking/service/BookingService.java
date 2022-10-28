package ru.practicum.shareit.server.booking.service;

import ru.practicum.shareit.dto.booking.BookingDto;
import ru.practicum.shareit.dto.booking.NewBookingDto;
import ru.practicum.shareit.dto.booking.State;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(long id, NewBookingDto newBookingDto);

    BookingDto getBooking(long id, long bookingId);

    List<BookingDto> getUserBookings(long id, State state, int from, int size);

    List<BookingDto> getBookingsByUserItems(long id, State state, int from, int size);

    BookingDto approveOrRejectBooking(long id, long bookingId, boolean approved);

    void deleteBooking(long id);
}
