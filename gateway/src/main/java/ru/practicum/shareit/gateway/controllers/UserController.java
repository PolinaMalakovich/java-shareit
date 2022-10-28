package ru.practicum.shareit.gateway.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.user.PatchUserDto;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.gateway.ShareItClient;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final ShareItClient shareItClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody final UserDto userDto) {
        return shareItClient.post("/users", userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable final long id) {
        return shareItClient.get("/users/" + id);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return shareItClient.get("/users");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable final long id, @Valid @RequestBody final PatchUserDto patchUserDto) {
        return shareItClient.patch("/users/" + id, patchUserDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable final long id) {
        shareItClient.delete("/users/" + id);
    }
}
