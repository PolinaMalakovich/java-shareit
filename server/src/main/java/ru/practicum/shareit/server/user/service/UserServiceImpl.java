package ru.practicum.shareit.server.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.user.PatchUserDto;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.server.exception.EntityNotFoundException;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;

import static ru.practicum.shareit.server.user.service.UserMapper.toUserDto;
import static ru.practicum.shareit.server.user.service.UserMapper.toUserDtoList;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(final UserDto userDto) {
        final User user = UserMapper.toUser(userDto);
        final User newUser = userRepository.save(user);
        final UserDto newUserDto = toUserDto(newUser);
        log.info("New user created successfully.");

        return newUserDto;
    }

    @Override
    public UserDto getUser(final long id) {
        final User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));

        return toUserDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        return toUserDtoList(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserDto updateUser(final long id, final PatchUserDto patchUserDto) {
        final User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        if (patchUserDto.getName() != null) {
            user.setName(patchUserDto.getName());
        }
        if (patchUserDto.getEmail() != null) {
            user.setEmail(patchUserDto.getEmail());
        }
        log.info("User " + user.getId() + " updated successfully.");

        return toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(final long id) {
        userRepository.deleteById(id);
        log.info("User " + id + " deleted successfully.");
    }
}
