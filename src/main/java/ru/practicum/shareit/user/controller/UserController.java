package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody final UserDto userDto) {
        return userService.addUser(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable final long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public Stream<UserDto> getUsers() { return userService.getUsers(); }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable final long id, @Valid @RequestBody final PatchUserDto patchUserDto) {
        return userService.updateUser(id, patchUserDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable final long id) {
        userService.deleteUser(id);
    }
}