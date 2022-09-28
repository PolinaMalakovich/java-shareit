package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") final long id,
                                 @Valid @RequestBody final NewBookingDto newBookingDto) {
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
                                            @RequestParam(required = false, defaultValue = "ALL") final State state) {
        return bookingService.getUserBookings(id, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByUserItems(@RequestHeader("X-Sharer-User-Id") final long id,
                                                   @RequestParam(required = false, defaultValue = "ALL")
                                                   final State state) {
        return bookingService.getBookingsByUserItems(id, state);
    }
}
