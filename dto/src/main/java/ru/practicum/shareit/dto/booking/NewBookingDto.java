package ru.practicum.shareit.dto.booking;

import lombok.Value;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class NewBookingDto {
    @NotNull
    @FutureOrPresent
    LocalDateTime start;
    @NotNull
    @FutureOrPresent
    LocalDateTime end;
    long itemId;

    @AssertTrue
    private boolean isStartBeforeEnd() {
        return start.isBefore(end);
    }
}
