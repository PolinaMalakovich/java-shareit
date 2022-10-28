package ru.practicum.shareit.server.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.dto.booking.*;
import ru.practicum.shareit.dto.booking.Status;
import ru.practicum.shareit.server.exception.UnauthorizedCommentException;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.dto.user.*;
import ru.practicum.shareit.dto.item.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private final UserDto alice = new UserDto(1L, "Alice", "alice.anderson@example.com");
    private final UserDto john = new UserDto(2L, "John", "john.doe@example.com");
    private final ItemDto coffeeTable = new ItemDto(3L, "Coffee table", "Old wooden coffee table", true, alice, null);
    private final CommentDto niceComment = new CommentDto(
        4L,
        "Nice!",
        coffeeTable.getId(),
        john.getId(),
        john.getName(),
        LocalDateTime.of(2021, 10, 16, 16, 47, 0)
    );
    private final BookingDtoBookerId lastBooking = new BookingDtoBookerId(
        5L,
        LocalDateTime.of(2021, 10, 13, 16, 47, 0),
        LocalDateTime.of(2021, 10, 15, 16, 47, 0),
        coffeeTable,
        john.getId(),
        Status.APPROVED
    );
    private final BookingDtoBookerId nextBooking = new BookingDtoBookerId(
        6L,
        LocalDateTime.of(2022, 10, 13, 16, 47, 0),
        LocalDateTime.of(2022, 12, 13, 16, 47, 0),
        coffeeTable,
        john.getId(),
        Status.APPROVED
    );
    private final ItemDtoWithCommentsAndBookings tableWithCommentsAndBookings = new ItemDtoWithCommentsAndBookings(
        coffeeTable.getId(),
        coffeeTable.getName(),
        coffeeTable.getDescription(),
        coffeeTable.getAvailable(),
        coffeeTable.getOwner(),
        null,
        List.of(niceComment),
        lastBooking,
        nextBooking
    );
    private final ListItemDto listItemDto = new ListItemDto(
        coffeeTable.getId(),
        coffeeTable.getName(),
        coffeeTable.getDescription(),
        coffeeTable.getAvailable(),
        List.of(niceComment),
        lastBooking,
        nextBooking
    );
    private final PatchItemDto patchItemDto = new PatchItemDto(
        coffeeTable.getName(),
        coffeeTable.getDescription(),
        coffeeTable.isAvailable()
    );
    private final String commentCreated = "2021-10-16T16:47:00";
    private final String lastBookingStart = "2021-10-13T16:47:00";
    private final String lastBookingEnd = "2021-10-15T16:47:00";
    private final String nextBookingStart = "2022-10-13T16:47:00";
    private final String nextBookingEnd = "2022-12-13T16:47:00";
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    @Test
    void addItem() throws Exception {
        Mockito.when(itemService.addItem(anyLong(), any())).thenReturn(coffeeTable);

        mvc.perform(
                post("/items")
                    .header("X-Sharer-User-Id", alice.getId())
                    .content(mapper.writeValueAsString(coffeeTable))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(coffeeTable.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(coffeeTable.getName())))
            .andExpect(jsonPath("$.description", is(coffeeTable.getDescription())))
            .andExpect(jsonPath("$.available", is(coffeeTable.isAvailable())))
            .andExpect(jsonPath("$.owner.id", is(coffeeTable.getOwner().getId()), Long.class))
            .andExpect(jsonPath("$.owner.name", is(coffeeTable.getOwner().getName())))
            .andExpect(jsonPath("$.owner.email", is(coffeeTable.getOwner().getEmail())))
            .andExpect(jsonPath("$.requestId", is(coffeeTable.getRequestId()), Long.class));
    }

    @Test
    void updateItem() throws Exception {
        Mockito.when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(coffeeTable);

        mvc.perform(
                patch("/items/{itemId}", coffeeTable.getId())
                    .header("X-Sharer-User-Id", alice.getId())
                    .content(mapper.writeValueAsString(patchItemDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(coffeeTable.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(coffeeTable.getName())))
            .andExpect(jsonPath("$.description", is(coffeeTable.getDescription())))
            .andExpect(jsonPath("$.available", is(coffeeTable.isAvailable())))
            .andExpect(jsonPath("$.owner.id", is(coffeeTable.getOwner().getId()), Long.class))
            .andExpect(jsonPath("$.owner.name", is(coffeeTable.getOwner().getName())))
            .andExpect(jsonPath("$.owner.email", is(coffeeTable.getOwner().getEmail())))
            .andExpect(jsonPath("$.requestId", is(coffeeTable.getRequestId()), Long.class));
    }

    @Test
    void getItem() throws Exception {
        Mockito
            .when(itemService.getItemWithCommentsAndBookings(anyLong(), anyLong()))
            .thenReturn(tableWithCommentsAndBookings);

        mvc.perform(
                get("/items/{itemId}", coffeeTable.getId())
                    .header("X-Sharer-User-Id", alice.getId())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(tableWithCommentsAndBookings.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(tableWithCommentsAndBookings.getName())))
            .andExpect(jsonPath("$.description", is(tableWithCommentsAndBookings.getDescription())))
            .andExpect(jsonPath("$.available", is(tableWithCommentsAndBookings.isAvailable())))
            .andExpect(jsonPath("$.owner.id", is(tableWithCommentsAndBookings.getOwner().getId()), Long.class))
            .andExpect(jsonPath("$.owner.name", is(tableWithCommentsAndBookings.getOwner().getName())))
            .andExpect(jsonPath("$.owner.email", is(tableWithCommentsAndBookings.getOwner().getEmail())))
            .andExpect(jsonPath("$.request", is(tableWithCommentsAndBookings.getRequest()), Long.class))
            .andExpect(
                jsonPath("$.comments[0].id", is(tableWithCommentsAndBookings.getComments().get(0).getId()), Long.class)
            )
            .andExpect(
                jsonPath("$.comments[0].text", is(tableWithCommentsAndBookings.getComments().get(0).getText()))
            )
            .andExpect(
                jsonPath(
                    "$.comments[0].itemId",
                    is(tableWithCommentsAndBookings.getComments().get(0).getItemId()), Long.class
                )
            )
            .andExpect(
                jsonPath(
                    "$.comments[0].authorId",
                    is(tableWithCommentsAndBookings.getComments().get(0).getAuthorId()), Long.class
                )
            )
            .andExpect(
                jsonPath(
                    "$.comments[0].authorName",
                    is(tableWithCommentsAndBookings.getComments().get(0).getAuthorName())
                )
            )
            .andExpect(jsonPath("$.comments[0].created", is(commentCreated)))


            .andExpect(
                jsonPath("$.lastBooking.id", is(tableWithCommentsAndBookings.getLastBooking().getId()), Long.class)
            )
            .andExpect(jsonPath("$.lastBooking.start", is(lastBookingStart)))
            .andExpect(jsonPath("$.lastBooking.end", is(lastBookingEnd)))
            .andExpect(
                jsonPath(
                    "$.lastBooking.item.id",
                    is(tableWithCommentsAndBookings.getLastBooking().getItem().getId()), Long.class
                )
            )
            .andExpect(
                jsonPath(
                    "$.lastBooking.bookerId",
                    is(tableWithCommentsAndBookings.getLastBooking().getBookerId()), Long.class)
            )


            .andExpect(
                jsonPath("$.nextBooking.id", is(tableWithCommentsAndBookings.getNextBooking().getId()), Long.class)
            )
            .andExpect(jsonPath("$.nextBooking.start", is(nextBookingStart)))
            .andExpect(jsonPath("$.nextBooking.end", is(nextBookingEnd)))
            .andExpect(
                jsonPath(
                    "$.nextBooking.item.id",
                    is(tableWithCommentsAndBookings.getNextBooking().getItem().getId()), Long.class)
            )
            .andExpect(
                jsonPath(
                    "$.nextBooking.bookerId",
                    is(tableWithCommentsAndBookings.getNextBooking().getBookerId()), Long.class
                )
            );
    }

    @Test
    void getItems() throws Exception {
        Mockito
            .when(itemService.getItems(anyLong(), anyInt(), anyInt()))
            .thenReturn(List.of(listItemDto));

        mvc.perform(
                get("/items")
                    .header("X-Sharer-User-Id", alice.getId())
                    .param("from", "0")
                    .param("size", "1")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(tableWithCommentsAndBookings.getId()), Long.class))
            .andExpect(jsonPath("$.[0].name", is(tableWithCommentsAndBookings.getName())))
            .andExpect(jsonPath("$.[0].description", is(tableWithCommentsAndBookings.getDescription())))
            .andExpect(jsonPath("$.[0].available", is(tableWithCommentsAndBookings.isAvailable())))
            .andExpect(
                jsonPath("$.[0].comments[0].id", is(tableWithCommentsAndBookings.getComments().get(0).getId()),
                    Long.class)
            )
            .andExpect(
                jsonPath("$.[0].comments[0].text", is(tableWithCommentsAndBookings.getComments().get(0).getText()))
            )
            .andExpect(
                jsonPath(
                    "$.[0].comments[0].itemId",
                    is(tableWithCommentsAndBookings.getComments().get(0).getItemId()), Long.class
                )
            )
            .andExpect(
                jsonPath(
                    "$.[0].comments[0].authorId",
                    is(tableWithCommentsAndBookings.getComments().get(0).getAuthorId()), Long.class
                )
            )
            .andExpect(
                jsonPath(
                    "$.[0].comments[0].authorName",
                    is(tableWithCommentsAndBookings.getComments().get(0).getAuthorName())
                )
            )
            .andExpect(jsonPath("$.[0].comments[0].created", is(commentCreated)))


            .andExpect(
                jsonPath("$.[0].lastBooking.id", is(tableWithCommentsAndBookings.getLastBooking().getId()), Long.class)
            )
            .andExpect(jsonPath("$.[0].lastBooking.start", is(lastBookingStart)))
            .andExpect(jsonPath("$.[0].lastBooking.end", is(lastBookingEnd)))
            .andExpect(
                jsonPath(
                    "$.[0].lastBooking.item.id",
                    is(tableWithCommentsAndBookings.getLastBooking().getItem().getId()), Long.class
                )
            )
            .andExpect(
                jsonPath(
                    "$.[0].lastBooking.bookerId",
                    is(tableWithCommentsAndBookings.getLastBooking().getBookerId()), Long.class)
            )


            .andExpect(
                jsonPath("$.[0].nextBooking.id", is(tableWithCommentsAndBookings.getNextBooking().getId()), Long.class)
            )
            .andExpect(jsonPath("$.[0].nextBooking.start", is(nextBookingStart)))
            .andExpect(jsonPath("$.[0].nextBooking.end", is(nextBookingEnd)))
            .andExpect(
                jsonPath(
                    "$.[0].nextBooking.item.id",
                    is(tableWithCommentsAndBookings.getNextBooking().getItem().getId()), Long.class)
            )
            .andExpect(
                jsonPath(
                    "$.[0].nextBooking.bookerId",
                    is(tableWithCommentsAndBookings.getNextBooking().getBookerId()), Long.class
                )
            );
    }

    @Test
    void deleteItem() throws Exception {
        itemService.deleteItem(anyLong(), anyLong());
        verify(itemService, times(1)).deleteItem(anyLong(), anyLong());

        mvc.perform(
                delete("/items/{itemId}", coffeeTable.getId())
                    .header("X-Sharer-User-Id", alice.getId())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }

    @Test
    void searchItem() throws Exception {
        Mockito
            .when(itemService.searchItem(anyString(), anyInt(), anyInt()))
            .thenReturn(List.of(coffeeTable));

        mvc.perform(
                get("/items/search")
                    .param("text", "table")
                    .param("from", "0")
                    .param("size", "1")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(coffeeTable.getId()), Long.class))
            .andExpect(jsonPath("$.[0].name", is(coffeeTable.getName())))
            .andExpect(jsonPath("$.[0].description", is(coffeeTable.getDescription())))
            .andExpect(jsonPath("$.[0].available", is(coffeeTable.isAvailable())))
            .andExpect(jsonPath("$.[0].owner.id", is(coffeeTable.getOwner().getId()), Long.class))
            .andExpect(jsonPath("$.[0].owner.name", is(coffeeTable.getOwner().getName())))
            .andExpect(jsonPath("$.[0].owner.email", is(coffeeTable.getOwner().getEmail())))
            .andExpect(jsonPath("$.[0].requestId", is(coffeeTable.getRequestId()), Long.class));
    }

    @Test
    void addComment() throws Exception {
        Mockito.when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(niceComment);

        mvc.perform(
                post("/items/{itemId}/comment", coffeeTable.getId())
                    .header("X-Sharer-User-Id", john.getId())
                    .content(mapper.writeValueAsString(niceComment))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(niceComment.getId()), Long.class))
            .andExpect(jsonPath("$.text", is(niceComment.getText())))
            .andExpect(jsonPath("$.itemId", is(niceComment.getItemId()), Long.class))
            .andExpect(jsonPath("$.authorId", is(niceComment.getAuthorId()), Long.class))
            .andExpect(jsonPath("$.authorName", is(niceComment.getAuthorName())))
            .andExpect(jsonPath("$.created", is(commentCreated)));
    }

    @Test
    void addCommentWhenUnauthorized() throws Exception {
        Mockito
            .when(itemService.addComment(anyLong(), anyLong(), any()))
            .thenThrow(new UnauthorizedCommentException(alice.getId(), coffeeTable.getId()));

        mvc.perform(
                post("/items/{itemId}/comment", coffeeTable.getId())
                    .header("X-Sharer-User-Id", alice.getId())
                    .content(mapper.writeValueAsString(niceComment))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest());
    }
}