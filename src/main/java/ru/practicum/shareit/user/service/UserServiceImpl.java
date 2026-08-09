package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Creating user in service, dto={}", userDto);
        User user = UserMapper.toUser(userDto);
        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            log.warn("Failed to create user. Email already exists: {}", userDto.getEmail());
            throw new ConflictException("Email already exists");
        }
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        log.info("Updating user in service, id={}, dto={}", userId, userDto);
        User existingUser = getUserOrThrow(userId);

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            existingUser.setEmail(userDto.getEmail());
        }

        try {
            userRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.warn("Failed to update user id={}. Email already exists: {}", userId, userDto.getEmail());
            throw new ConflictException("Email already exists");
        }

        return UserMapper.toUserDto(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long userId) {
        log.info("Getting user in service, id={}", userId);
        return UserMapper.toUserDto(getUserOrThrow(userId));
    }

    @Override
    public void delete(Long userId) {
        log.info("Deleting user in service, id={}", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("User with id={} not found for deletion", userId);
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with id={} not found", userId);
                    return new NotFoundException("User not found");
                });
    }
}
