package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateValueException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Stream;

@Component
public final class UserDaoImpl implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long id = 1;

    @Override
    public User addUser(final User user) {
        final String email = user.getEmail();
        if (emails.contains(email)) {
            throw new DuplicateValueException(email);
        }
        final User newUser = user.withId(id++);
        emails.add(email);
        users.put(newUser.getId(), newUser);

        return newUser;
    }

    @Override
    public Optional<User> getUser(final long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Stream<User> getUsers() {
        return users.values().stream();
    }

    @Override
    public Optional<User> updateUser(final User user) {
        return getUser(user.getId())
            .map(u -> {
                final String email = user.getEmail();
                if (!email.equals(u.getEmail()) && emails.contains(email)) {
                    throw new DuplicateValueException(email);
                }
                emails.remove(u.getEmail());
                emails.add(email);
                users.replace(user.getId(), user);
                return user;
            });
    }

    @Override
    public void deleteUser(final long id) {
        final User user = getUser(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        final String email = user.getEmail();
        emails.remove(email);
        users.remove(id);
    }
}
