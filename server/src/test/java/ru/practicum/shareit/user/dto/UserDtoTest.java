package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static ru.practicum.shareit.VarsTestList.*;


public class UserDtoTest {

    public static final UserDto INVALID_USER_DTO_BLANK_NAME = new UserDto(null, "   ", VALID_EMAIL_1);
    public static final UserDto INVALID_USER_DTO_INVALID_EMAIL = new UserDto(null, VALID_USER_NAME_1, INVALID_EMAIL);
    public static final UserDto INVALID_USER_DTO_NULL_EMAIL = new UserDto(null, VALID_USER_NAME_1, null);
    public static final UserDto INVALID_USER_DTO_NULL_NAME = new UserDto(null, null, VALID_EMAIL_1);
    public static final UserDto VALID_USER_DTO_1 = new UserDto(null, VALID_USER_NAME_1, VALID_EMAIL_1);
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Valid user dto validation")
    public void validate_validUserDto_noViolations() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(VALID_USER_DTO_1);
        Assertions.assertTrue(violations.isEmpty(), "Expected valid user dto without violations");
    }

    @Test
    @DisplayName("UserDto with null name validation")
    public void validate_userDtoWithNullName_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_NULL_NAME);
        Assertions.assertFalse(violations.isEmpty(), "Expected violation for null name");
    }

    @Test
    @DisplayName("UserDto with blank name validation")
    public void validate_userDtoWithBlankName_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_BLANK_NAME);
        Assertions.assertFalse(violations.isEmpty(), "Expected violation for blank name");
    }

    @Test
    @DisplayName("UserDto with null email validation")
    public void validate_userDtoWithNullEmail_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_NULL_EMAIL);
        Assertions.assertFalse(violations.isEmpty(), "Expected violation for null email");
    }

    @Test
    @DisplayName("UserDto with invalid email validation")
    public void validate_userDtoWithInvalidEmail_hasViolation() {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(INVALID_USER_DTO_INVALID_EMAIL);
        Assertions.assertFalse(violations.isEmpty(), "Expected violation for invalid email");
    }
}
