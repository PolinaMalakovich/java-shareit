package ru.practicum.shareit.server.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.dto.request.*;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.dto.user.*;
import ru.practicum.shareit.dto.item.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private final UserDto john = new UserDto(1L, "John", "john.doe@example.com");
    private final UserDto alice = new UserDto(2L, "Alice", "alice.anderson@example.com");
    private final ItemRequestDto tableRequest = new ItemRequestDto(
        4L,
        "Vintage coffee table, preferably wooden",
        john,
        LocalDateTime.of(2022, 10, 11, 15, 14, 0)
    );
    private final ItemDto coffeeTable = new ItemDto(
        3L,
        "Coffee table",
        "Old wooden coffee table",
        true,
        alice,
        tableRequest.getId()
    );
    private final ItemDtoForRequests tableForRequests = new ItemDtoForRequests(
        coffeeTable.getId(),
        coffeeTable.getName(),
        coffeeTable.getDescription(),
        coffeeTable.isAvailable(),
        coffeeTable.getRequestId()
    );
    private final ItemRequestDtoWithAnswers tableRequestWithAnswers = new ItemRequestDtoWithAnswers(
        tableRequest.getId(),
        tableRequest.getDescription(),
        tableRequest.getCreated(),
        List.of(tableForRequests)
    );
    private final String requestCreated = "2022-10-11T15:14:00";
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;

    @Test
    void addRequest() throws Exception {
        Mockito.when(itemRequestService.addRequest(anyLong(), any())).thenReturn(tableRequest);

        mvc.perform(
                post("/requests")
                    .header("X-Sharer-User-Id", john.getId())
                    .content(mapper.writeValueAsString(tableRequest))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(tableRequest.getId()), Long.class))
            .andExpect(jsonPath("$.description", is(tableRequest.getDescription())))
            .andExpect(jsonPath("$.requester.id", is(tableRequest.getRequester().getId()), Long.class))
            .andExpect(jsonPath("$.requester.name", is(tableRequest.getRequester().getName())))
            .andExpect(jsonPath("$.requester.email", is(tableRequest.getRequester().getEmail())))
            .andExpect(jsonPath("$.created", is(requestCreated)));
    }

    @Test
    void getRequests() throws Exception {
        Mockito.when(itemRequestService.getRequests(anyLong())).thenReturn(List.of(tableRequestWithAnswers));

        mvc.perform(
                get("/requests")
                    .header("X-Sharer-User-Id", john.getId())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(tableRequestWithAnswers.getId()), Long.class))
            .andExpect(jsonPath("$.[0].description", is(tableRequestWithAnswers.getDescription())))
            .andExpect(jsonPath("$.[0].created", is(requestCreated)))
            .andExpect(jsonPath("$.[0].items[0].id", is(tableRequestWithAnswers.getItems().get(0).getId()), Long.class))
            .andExpect(jsonPath("$.[0].items[0].name", is(tableRequestWithAnswers.getItems().get(0).getName())))
            .andExpect(
                jsonPath("$.[0].items[0].description", is(tableRequestWithAnswers.getItems().get(0).getDescription()))
            )
            .andExpect(
                jsonPath("$.[0].items[0].available", is(tableRequestWithAnswers.getItems().get(0).isAvailable()))
            )
            .andExpect(
                jsonPath(
                    "$.[0].items[0].requestId",
                    is(tableRequestWithAnswers.getItems().get(0).getRequestId()), Long.class
                )
            );
    }

    @Test
    void getAllRequests() throws Exception {
        Mockito
            .when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
            .thenReturn(List.of(tableRequestWithAnswers));

        mvc.perform(
                get("/requests/all")
                    .header("X-Sharer-User-Id", john.getId())
                    .param("from", "0")
                    .param("size", "1")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(tableRequestWithAnswers.getId()), Long.class))
            .andExpect(jsonPath("$.[0].description", is(tableRequestWithAnswers.getDescription())))
            .andExpect(jsonPath("$.[0].created", is(requestCreated)))
            .andExpect(jsonPath("$.[0].items[0].id", is(tableRequestWithAnswers.getItems().get(0).getId()), Long.class))
            .andExpect(jsonPath("$.[0].items[0].name", is(tableRequestWithAnswers.getItems().get(0).getName())))
            .andExpect(
                jsonPath("$.[0].items[0].description", is(tableRequestWithAnswers.getItems().get(0).getDescription()))
            )
            .andExpect(
                jsonPath("$.[0].items[0].available", is(tableRequestWithAnswers.getItems().get(0).isAvailable()))
            )
            .andExpect(
                jsonPath(
                    "$.[0].items[0].requestId",
                    is(tableRequestWithAnswers.getItems().get(0).getRequestId()), Long.class
                )
            );
    }

    @Test
    void getRequest() throws Exception {
        Mockito
            .when(itemRequestService.getRequest(anyLong(), anyLong()))
            .thenReturn(tableRequestWithAnswers);

        mvc.perform(
                get("/requests/{requestId}", tableRequestWithAnswers.getId())
                    .header("X-Sharer-User-Id", john.getId())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(tableRequestWithAnswers.getId()), Long.class))
            .andExpect(jsonPath("$.description", is(tableRequestWithAnswers.getDescription())))
            .andExpect(jsonPath("$.created", is(requestCreated)))
            .andExpect(jsonPath("$.items[0].id", is(tableRequestWithAnswers.getItems().get(0).getId()), Long.class))
            .andExpect(jsonPath("$.items[0].name", is(tableRequestWithAnswers.getItems().get(0).getName())))
            .andExpect(
                jsonPath("$.items[0].description", is(tableRequestWithAnswers.getItems().get(0).getDescription()))
            )
            .andExpect(
                jsonPath("$.items[0].available", is(tableRequestWithAnswers.getItems().get(0).isAvailable()))
            )
            .andExpect(
                jsonPath(
                    "$.items[0].requestId",
                    is(tableRequestWithAnswers.getItems().get(0).getRequestId()), Long.class
                )
            );
    }
}