package ru.practicum.shareit.gateway.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.booking.NewBookingDto;
import ru.practicum.shareit.dto.booking.State;
import ru.practicum.shareit.gateway.ShareItClient;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final ShareItClient shareItClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") final long id,
                                             @Valid @RequestBody final NewBookingDto newBookingDto) {
        return shareItClient.post("/bookings", id, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveOrRejectBooking(@RequestHeader("X-Sharer-User-Id") final long id,
                                                         @PathVariable final long bookingId,
                                                         @RequestParam final boolean approved) {
        return shareItClient.patch("/bookings/" + bookingId + "?approved={approved}", id, Map.of("approved", String.valueOf(approved)), null);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") final long id,
                                             @PathVariable final long bookingId) {
        return shareItClient.get("/bookings/" + bookingId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") final long id,
                                                  @RequestParam(required = false, defaultValue = "ALL")
                                                  final State state,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                                  @RequestParam(defaultValue = "100") @Positive final int size) {
        return shareItClient.get(
            "/bookings?state={state}&from={from}&size={size}",
            id,
            Map.ofEntries(
                Map.entry("state", state),
                Map.entry("from", from),
                Map.entry("size", size)
            )
        );
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByUserItems(
        @RequestHeader("X-Sharer-User-Id") final long id,
        @RequestParam(required = false, defaultValue = "ALL") final State state,
        @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
        @RequestParam(defaultValue = "100") @Positive final int size
    ) {
        return shareItClient.get(
            "/bookings/owner?state={state}&from={from}&size={size}",
            id,
            Map.ofEntries(
                Map.entry("state", state),
                Map.entry("from", from),
                Map.entry("size", size)
            )
        );
    }
}
