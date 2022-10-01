package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.Value;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Value
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookedItem item;
    Booker booker;
    Status status;

    @Data
    public static class BookedItem {
        private final long id;
        private final String name;
    }

    @Data
    public static class Booker {
        private final long id;
        private final String name;
    }
}
