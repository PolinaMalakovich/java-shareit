package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(long id, NewBookingDto newBookingDto);

    BookingDto getBooking(long id, long bookingId);

    List<BookingDto> getUserBookings(long id, State state);

    List<BookingDto> getBookingsByUserItems(long id, State state);

    BookingDto approveOrRejectBooking(long id, long bookingId, boolean approved);

    void deleteBooking(long id);
}
