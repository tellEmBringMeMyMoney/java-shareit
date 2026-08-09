package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
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
        validateEmailUniqueness(userDto.getEmail(), null);
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.create(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        log.info("Updating user in service, id={}, dto={}", userId, userDto);
        User existingUser = getUserOrThrow(userId);

        if (validateName(userDto.getName())) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            validateEmail(userDto.getEmail());
            validateEmailUniqueness(userDto.getEmail(), userId);
            existingUser.setEmail(userDto.getEmail());
        }

        return UserMapper.toUserDto(userRepository.update(existingUser));
    }

    @Override
    public UserDto getById(Long userId) {
        log.info("Getting user in service, id={}", userId);
        return UserMapper.toUserDto(getUserOrThrow(userId));
    }

    @Override
    public void delete(Long userId) {
        log.info("Deleting user in service, id={}", userId);
        getUserOrThrow(userId);
        userRepository.delete(userId);
    }

    private void validateEmailUniqueness(String email, Long currentUserId) {
        if (email == null) {
            return;
        }
        userRepository.findByEmail(email)
                .filter(user -> !user.getId().equals(currentUserId))
                .ifPresent(user -> {
                    log.warn("Failed unique check on email={}, currentUserId={}", email, currentUserId);
                    throw new ConflictException("Email already exists");
                });
    }

    private void validateEmail(String email) {
        if (email.isBlank() || !email.contains("@")) {
            log.warn("Email validation failed for email='{}'", email);
            throw new NotFoundException("User email must be valid");
        }
    }

    private boolean validateName(String name) {
        return name != null && !name.isBlank();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with id={} not found", userId);
                    return new NotFoundException("User not found");
                });
    }
}
