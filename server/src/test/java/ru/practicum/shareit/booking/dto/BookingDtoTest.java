package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    @DisplayName("Serialize booking dto")
    public void serialize_bookingDto_containsExpectedFields() throws Exception {
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2026, 8, 10, 12, 0),
                LocalDateTime.of(2026, 8, 11, 12, 0),
                3L,
                null,
                new UserDto(2L, "Victor", "vi@ct.or"),
                Status.APPROVED
        );

        assertThat(json.write(bookingDto)).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.start").isEqualTo("2026-08-10T12:00:00");
        assertThat(json.write(bookingDto)).extractingJsonPathNumberValue("$.itemId").isEqualTo(3);
        assertThat(json.write(bookingDto)).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.status").isEqualTo(Status.APPROVED.name());
    }
}
