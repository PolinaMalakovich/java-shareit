package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    private final UserDto alice = new UserDto(1L, "Alice", "alice.anderson@example.com");
    private final PatchUserDto patchAlice = new PatchUserDto("Alice", "alice.anderson@example.com");
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mvc;

    @Test
    void addUser() throws Exception {
        Mockito.when(userService.addUser(any())).thenReturn(alice);

        mvc.perform(
                post("/users")
                    .content(mapper.writeValueAsString(alice))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(alice.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(alice.getName())))
            .andExpect(jsonPath("$.email", is(alice.getEmail())));
    }

    @Test
    void getUser() throws Exception {
        Mockito.when(userService.getUser(anyLong())).thenReturn(alice);

        mvc.perform(
                get("/users/{id}", alice.getId())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(alice.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(alice.getName())))
            .andExpect(jsonPath("$.email", is(alice.getEmail())));
    }

    @Test
    void getUsers() throws Exception {
        Mockito.when(userService.getUsers()).thenReturn(List.of(alice));

        mvc.perform(
                get("/users")
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id", is(alice.getId()), Long.class))
            .andExpect(jsonPath("$.[0].name", is(alice.getName())))
            .andExpect(jsonPath("$.[0].email", is(alice.getEmail())));
    }

    @Test
    void updateUser() throws Exception {
        Mockito.when(userService.updateUser(anyLong(), any())).thenReturn(alice);

        mvc.perform(
                patch("/users/{id}", alice.getId())
                    .content(mapper.writeValueAsString(patchAlice))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(alice.getId()), Long.class))
            .andExpect(jsonPath("$.name", is(alice.getName())))
            .andExpect(jsonPath("$.email", is(alice.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        userService.deleteUser(anyLong());
        Mockito.verify(userService, times(1)).deleteUser(anyLong());

        mvc.perform(
                delete("/users/{id}", alice.getId())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk());
    }
}