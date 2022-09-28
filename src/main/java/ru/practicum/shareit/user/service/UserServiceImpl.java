package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(final UserDto userDto) {
        final User user = UserMapper.toUser(userDto);
        final User newUser = userRepository.save(user);
        final UserDto newUserDto = UserMapper.toUserDto(newUser);
        log.info("New user created successfully.");

        return newUserDto;
    }

    @Override
    public UserDto getUser(final long id) {
        final User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(final long id, final PatchUserDto patchUserDto) {
        final UserDto user = getUser(id);
        final UserDto userDto = patchUserDto.patch(user);
        userRepository.findById(userDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("User", userDto.getId()));
        userRepository.save(UserMapper.toUser(userDto));
        log.info("User " + userDto.getId() + " updated successfully.");

        return userDto;
    }

    @Override
    public void deleteUser(final long id) {
        userRepository.deleteById(id);
        log.info("User " + id + " deleted successfully.");
    }
}
