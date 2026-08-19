package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestBody @Valid BookItemRequestDto requestDto)
            throws BadRequestException {
        if (!requestDto.getEnd().isAfter(requestDto.getStart())) {
            throw new BadRequestException("Booking end must be after start");
        }
        return bookingClient.create(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable @Positive Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable @Positive Long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(USER_ID_HEADER) long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getUserBookings(userId, parseState(state));
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(USER_ID_HEADER) long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getOwnerBookings(userId, parseState(state));
    }

    private BookingState parseState(String value) {
        return BookingState.from(value)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + value));
    }
}
