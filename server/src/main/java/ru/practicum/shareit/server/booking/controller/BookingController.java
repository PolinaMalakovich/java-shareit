package ru.practicum.shareit.server.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.booking.BookingDto;
import ru.practicum.shareit.dto.booking.NewBookingDto;
import ru.practicum.shareit.dto.booking.State;
import ru.practicum.shareit.server.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") final long id,
                                 @RequestBody final NewBookingDto newBookingDto) {
        return bookingService.addBooking(id, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@RequestHeader("X-Sharer-User-Id") final long id,
                                             @PathVariable final long bookingId,
                                             @RequestParam final boolean approved) {
        return bookingService.approveOrRejectBooking(id, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") final long id,
                                 @PathVariable final long bookingId) {
        return bookingService.getBooking(id, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") final long id,
                                            @RequestParam(required = false, defaultValue = "ALL") final State state,
                                            @RequestParam(defaultValue = "0") final int from,
                                            @RequestParam(defaultValue = "100") final int size) {
        return bookingService.getUserBookings(id, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByUserItems(
        @RequestHeader("X-Sharer-User-Id") final long id,
        @RequestParam(required = false, defaultValue = "ALL") final State state,
        @RequestParam(defaultValue = "0") final int from,
        @RequestParam(defaultValue = "100") final int size
    ) {
        return bookingService.getBookingsByUserItems(id, state, from, size);
    }
}
