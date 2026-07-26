package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    User update(User user);

    Optional<User> findById(Long userId);

    Collection<User> findAll();

    Optional<User> findByEmail(String email);

    void delete(Long userId);
}
