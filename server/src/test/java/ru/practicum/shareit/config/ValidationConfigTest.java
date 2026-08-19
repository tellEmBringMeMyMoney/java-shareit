package ru.practicum.shareit.config;

import jakarta.validation.Validator;
import jakarta.validation.constraints.FutureOrPresent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ValidationConfigTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldAllowSmallTemporalDifference() {
        TestDto dto = new TestDto();
        dto.start = LocalDateTime.now().minusSeconds(1);

        var violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    private static class TestDto {

        @FutureOrPresent
        private LocalDateTime start;
    }

    @Test
    void shouldRejectDateOutsideTolerance() {
        TestDto dto = new TestDto();
        dto.start = LocalDateTime.now().minusSeconds(5);

        var violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}
