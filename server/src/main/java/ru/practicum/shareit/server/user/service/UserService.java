package ru.practicum.shareit.server.user.service;

import ru.practicum.shareit.dto.user.PatchUserDto;
import ru.practicum.shareit.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto getUser(long id);

    List<UserDto> getUsers();

    UserDto updateUser(long id, PatchUserDto patchUserDto);

    void deleteUser(long id);
}
