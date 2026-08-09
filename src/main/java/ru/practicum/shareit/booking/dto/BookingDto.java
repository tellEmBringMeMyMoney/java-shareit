package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(message = "Booking start can not be empty")
    private LocalDateTime start;
    @NotNull(message = "Booking end can not be empty")
    private LocalDateTime end;
    @NotNull(message = "Booked item id can not be empty")
    private Long itemId;
    private ItemDto item;
    private UserDto booker;
    private Status status;
}
