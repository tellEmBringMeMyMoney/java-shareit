package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void shouldCreateBookingUsingBuilderAndGetters() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();

        assertEquals(1L, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    void shouldSetBookingFields() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        Booking booking = new Booking();

        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(Status.APPROVED);

        assertEquals(1L, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(Status.APPROVED, booking.getStatus());
    }

    @Test
    void shouldBeEqualToItself() {
        Booking booking = Booking.builder()
                .id(1L)
                .build();

        assertEquals(booking, booking);
    }

    @Test
    void shouldNotBeEqualToNull() {
        Booking booking = Booking.builder()
                .id(1L)
                .build();

        assertNotEquals(null, booking);
    }

    @Test
    void shouldBeEqualWhenIdsAreEqual() {
        Booking first = Booking.builder()
                .id(1L)
                .status(Status.WAITING)
                .build();

        Booking second = Booking.builder()
                .id(1L)
                .status(Status.APPROVED)
                .build();

        assertEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenIdsAreDifferent() {
        Booking first = Booking.builder().id(1L).build();
        Booking second = Booking.builder().id(2L).build();

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenIdIsNull() {
        Booking first = Booking.builder().id(null).build();
        Booking second = Booking.builder().id(null).build();

        assertNotEquals(first, second);
    }

    @Test
    void shouldReturnSameHashCodeForBookingsOfSameClass() {
        Booking first = new Booking();
        Booking second = new Booking();

        assertEquals(first.hashCode(), second.hashCode());
    }
}
