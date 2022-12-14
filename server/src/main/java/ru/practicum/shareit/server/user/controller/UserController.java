package ru.practicum.shareit.server.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.user.PatchUserDto;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody final UserDto userDto) {
        return userService.addUser(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable final long id) {
        return userService.getUser(id);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable final long id, @RequestBody final PatchUserDto patchUserDto) {
        return userService.updateUser(id, patchUserDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable final long id) {
        userService.deleteUser(id);
    }
}
