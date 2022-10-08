package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    private final User alice = new User(1L, "Alice", "alice.anderson@example.com");
    private final User john = new User(2L, "John", "john.doe@example.com");
    private final Item coffeeTable = new Item(3L, "Coffee table", "Old wooden coffee table", true, alice, null);
    private final BookingDto.BookedItem bookedItem = new BookingDto.BookedItem(
        coffeeTable.getId(),
        coffeeTable.getName()
    );
    private final BookingDto.Booker booker = new BookingDto.Booker(john.getId(), john.getName());
    private final BookingDto bookingDto = new BookingDto(
        4L,
        LocalDateTime.of(2023, 10, 13, 16, 47),
        LocalDateTime.of(2023, 10, 15, 16, 47),
        bookedItem,
        booker,
        Status.WAITING);
    private final BookingDto approved = new BookingDto(
        bookingDto.getId(),
        bookingDto.getStart(),
        bookingDto.getEnd(),
        bookingDto.getItem(),
        bookingDto.getBooker(),
        Status.APPROVED
    );
    private final NewBookingDto newBookingDto = new NewBookingDto(
        bookingDto.getStart(),
        bookingDto.getEnd(),
        bookingDto.getBooker().getId()
    );
    private final String startDate = "2023-10-13T16:47:00";
    private final String endDate = "2023-10-15T16:47:00";

    @Test
    void addBooking() throws Exception {
        Mockito.when(bookingService.addBooking(anyLong(), any())).thenReturn(bookingDto);

        mvc.perform(
                post("/bookings")
                    .header("X-Sharer-User-Id", john.getId())
                    .content(mapper.writeValueAsString(newBookingDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
            .andExpect(jsonPath("$.start", is(startDate)))
            .andExpect(jsonPath("$.end", is(endDate)))
            .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
            .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
            .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
            .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
            .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));
    }

    @Test
    void approveOrRejectBooking() throws Exception {
        Mockito
            .when(bookingService.approveOrRejectBooking(anyLong(), anyLong(), eq(true)))
            .thenReturn(approved);

        mvc.perform(
                patch("/bookings/{bookingId}", bookingDto.getId())
                    .header("X-Sharer-User-Id", john.getId())
                    .param("approved", "true")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(approved.getId()), Long.class))
            .andExpect(jsonPath("$.start", is(startDate)))
            .andExpect(jsonPath("$.end", is(endDate)))
            .andExpect(jsonPath("$.item.id", is(approved.getItem().getId()), Long.class))
            .andExpect(jsonPath("$.item.name", is(approved.getItem().getName())))
            .andExpect(jsonPath("$.booker.id", is(approved.getBooker().getId()), Long.class))
            .andExpect(jsonPath("$.booker.name", is(approved.getBooker().getName())))
            .andExpect(jsonPath("$.status", is(approved.getStatus().name())));
    }

    @Test
    void getBooking() throws Exception {
        Mockito.when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(
                get("/bookings/{bookingId}", bookingDto.getId())
                    .header("X-Sharer-User-Id", john.getId())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
            .andExpect(jsonPath("$.start", is(startDate)))
            .andExpect(jsonPath("$.end", is(endDate)))
            .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
            .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
            .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
            .andExpect(jsonPath("$.booker.name", is(bookingDto.getBooker().getName())))
            .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));
    }

    @Test
    void getUserBookings() throws Exception {
        Mockito
            .when(bookingService.getUserBookings(anyLong(), any(), anyInt(), anyInt()))
            .thenReturn(List.of(bookingDto));

        mvc.perform(
                get("/bookings")
                    .header("X-Sharer-User-Id", john.getId())
                    .param("state", "ALL")
                    .param("from", "0")
                    .param("size", "1")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
            .andExpect(jsonPath("$.[0].start", is(startDate)))
            .andExpect(jsonPath("$.[0].end", is(endDate)))
            .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
            .andExpect(jsonPath("$.[0].item.name", is(bookingDto.getItem().getName())))
            .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
            .andExpect(jsonPath("$.[0].booker.name", is(bookingDto.getBooker().getName())))
            .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().name())));
    }

    @Test
    void getBookingsByUserItems() throws Exception {
        Mockito
            .when(bookingService.getBookingsByUserItems(anyLong(), any(), anyInt(), anyInt()))
            .thenReturn(List.of(bookingDto));

        mvc.perform(
                get("/bookings/owner")
                    .header("X-Sharer-User-Id", john.getId())
                    .param("state", "ALL")
                    .param("from", "0")
                    .param("size", "1")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
            .andExpect(jsonPath("$.[0].start", is(startDate)))
            .andExpect(jsonPath("$.[0].end", is(endDate)))
            .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
            .andExpect(jsonPath("$.[0].item.name", is(bookingDto.getItem().getName())))
            .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
            .andExpect(jsonPath("$.[0].booker.name", is(bookingDto.getBooker().getName())))
            .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().name())));
    }
}