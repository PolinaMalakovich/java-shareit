package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.stream.Stream;

public interface UserService {
    UserDto addUser(UserDto userDto);
    UserDto getUser(long id);
    Stream<UserDto> getUsers();
    UserDto updateUser(long id, PatchUserDto patchUserDto);
    void deleteUser(long id);
}
