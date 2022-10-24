package ru.practicum.shareit.dto.booking;

import lombok.Data;
import lombok.Value;

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
