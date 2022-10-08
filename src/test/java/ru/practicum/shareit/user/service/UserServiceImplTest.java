package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private final long randomId = 42;
    private final User john = new User(1L, "John", "john.doe@example.com");
    private final UserDto johnDto = new UserDto(john.getId(), john.getName(), john.getEmail());
    @Mock
    UserRepository mockUserRepository;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    void addUser() {
        Mockito.when(mockUserRepository.save(any())).thenReturn(john);
        UserDto actual = userService.addUser(johnDto);

        assertEquals(johnDto, actual);
    }

    @Test
    void getUserWhenUserDoesNotExist() {
        Mockito.when(mockUserRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(randomId));
    }

    @Test
    void getUser() {
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        UserDto actual = userService.getUser(john.getId());

        assertEquals(johnDto, actual);
    }

    @Test
    void getUsers() {
        Mockito.when(mockUserRepository.findAll()).thenReturn(List.of(john));
        List<UserDto> actual = userService.getUsers();

        assertEquals(List.of(johnDto), actual);
    }

    @Test
    void updateUserWhenUserDoesNotExist() {
        PatchUserDto patchAlice = new PatchUserDto("Alyson", "alyson.anderson@example.com");
        Mockito.when(mockUserRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(randomId, patchAlice));
    }

    @Test
    void updateUserWithNulls() {
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        UserDto aliceDto = new UserDto(alice.getId(), alice.getName(), alice.getEmail());
        PatchUserDto patchAlice = new PatchUserDto(null, null);
        Mockito.when(mockUserRepository.findById(alice.getId())).thenReturn(Optional.of(alice));
        UserDto actual = userService.updateUser(alice.getId(), patchAlice);

        assertEquals(aliceDto, actual);
    }

    @Test
    void updateUser() {
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        PatchUserDto patchAlice = new PatchUserDto("Alyson", "alyson.anderson@example.com");
        UserDto alyson = new UserDto(alice.getId(), patchAlice.getName(), patchAlice.getEmail());
        Mockito.when(mockUserRepository.findById(alice.getId())).thenReturn(Optional.of(alice));
        UserDto actual = userService.updateUser(alice.getId(), patchAlice);

        assertEquals(alyson, actual);
    }

    @Test
    void deleteUser() {
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        userService.deleteUser(alice.getId());
        Mockito.verify(mockUserRepository, Mockito.times(1)).deleteById(alice.getId());
    }
}