package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.stream.Stream;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(final UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDto addUser(final UserDto userDto) {
        final User user = UserMapper.toUser(userDto);
        final User newUser = userDao.addUser(user);
        final UserDto newUserDto = UserMapper.toUserDto(newUser);
        log.info("New user created successfully.");

        return newUserDto;
    }

    @Override
    public UserDto getUser(final long id) {
        final User user = userDao.getUser(id).orElseThrow(() -> new EntityNotFoundException("User", id));

        return UserMapper.toUserDto(user);
    }

    @Override
    public Stream<UserDto> getUsers() {
        return userDao.getUsers().map(UserMapper::toUserDto);
    }

    @Override
    public UserDto updateUser(final long id, final PatchUserDto patchUserDto) {
        final UserDto user = getUser(id);
        final UserDto userDto = patchUserDto.patch(user);
        userDao.updateUser(UserMapper.toUser(userDto))
            .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        log.info("User " + userDto.getId() + " updated successfully.");

        return userDto;
    }

    @Override
    public void deleteUser(final long id) {
        userDao.deleteUser(id);
        log.info("User " + id + " deleted successfully.");
    }
}
