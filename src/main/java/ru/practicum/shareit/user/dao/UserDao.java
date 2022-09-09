package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserDao {
    User addUser(User user);
    Optional<User> getUser(long id);
    Stream<User> getUsers();
    Optional<User> updateUser(User user);
    void deleteUser(long id);
}
