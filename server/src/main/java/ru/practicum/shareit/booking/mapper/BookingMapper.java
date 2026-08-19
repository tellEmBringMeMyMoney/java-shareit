package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    private BookingMapper() {
    }

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        Long itemId = booking.getItem() != null ? booking.getItem().getId() : null;
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemId,
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDto dto, User booker, Item item, Status status) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }

    public static BookingDtoShort toBookingDtoShort(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }

        Long bookerId = null;
        if (bookingDto.getBooker() != null) {
            bookerId = bookingDto.getBooker().getId();
        }

        return BookingDtoShort.builder()
                .id(bookingDto.getId())
                .bookerId(bookerId)
                .build();
    }
}
