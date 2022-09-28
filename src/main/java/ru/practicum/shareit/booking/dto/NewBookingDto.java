package ru.practicum.shareit.booking.dto;

import lombok.Value;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Value
public class NewBookingDto {
    @Future
    LocalDateTime start;
    @Future
    LocalDateTime end;
    long itemId;

    @AssertTrue
    private boolean isStartBeforeEnd() {
        return start.isBefore(end);
    }
}
