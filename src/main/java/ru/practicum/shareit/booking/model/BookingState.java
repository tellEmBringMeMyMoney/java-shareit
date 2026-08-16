package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ForbiddenException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState from(String state) {
        if (state == null) {
            throw new ForbiddenException("Null booking state");
        }
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ForbiddenException("Bad state: " + state);
        }
    }
}
