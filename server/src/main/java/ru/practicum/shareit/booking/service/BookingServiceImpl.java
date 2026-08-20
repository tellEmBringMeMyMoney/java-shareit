package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final Sort START_DESC = Sort.by(Sort.Direction.DESC, "start");

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        log.info("Creating booking for userId={}, bookingDto={}", userId, bookingDto);
        User booker = getUserOrThrowNotFound(userId);
        Item item = getItemOrThrow(bookingDto.getItemId());

        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Item owner cannot book own item");
        }
        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new BadRequestException("Item is unavailable");
        }
        validateDates(bookingDto.getStart(), bookingDto.getEnd());

        Booking booking = BookingMapper.toBooking(bookingDto, booker, item, Status.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        log.info("Changing approval for bookingId={} by userId={}, approved={}", bookingId, userId, approved);
        getUserOrThrowBadRequest(userId);
        Booking booking = getBookingOrThrow(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Booking not found for user id = " + userId);
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new BadRequestException("Booking status can be changed only while waiting");
        }

        booking.setStatus(Boolean.TRUE.equals(approved) ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long userId, Long bookingId) {
        log.info("Getting booking Id={} for userId={}", bookingId, userId);
        getUserOrThrowNotFound(userId);
        Booking booking = getBookingOrThrow(bookingId);

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only booker or item owner can view booking");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        log.info("Getting bookings for userId={}, state={}", userId, state);
        getUserOrThrowNotFound(userId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBooker_Id(userId, START_DESC);
            case CURRENT -> bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(userId, now, now, START_DESC);
            case PAST -> bookingRepository.findByBooker_IdAndEndBefore(userId, now, START_DESC);
            case FUTURE -> bookingRepository.findByBooker_IdAndStartAfter(userId, now, START_DESC);
            case WAITING -> bookingRepository.findByBooker_IdAndStatus(userId, Status.WAITING, START_DESC);
            case REJECTED -> bookingRepository.findByBooker_IdAndStatus(userId, Status.REJECTED, START_DESC);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getOwnerBookings(Long userId, BookingState state) {
        log.info("Getting owner bookings for userId={}, state={}", userId, state);
        getUserOrThrowNotFound(userId);
        LocalDateTime now = LocalDateTime.now();


        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItem_Owner_Id(userId, START_DESC);
            case CURRENT ->
                    bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(userId, now, now, START_DESC);
            case PAST -> bookingRepository.findByItem_Owner_IdAndEndBefore(userId, now, START_DESC);
            case FUTURE -> bookingRepository.findByItem_Owner_IdAndStartAfter(userId, now, START_DESC);
            case WAITING -> bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.WAITING, START_DESC);
            case REJECTED -> bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.REJECTED, START_DESC);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();

    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new BadRequestException("Booking end must be after start");
        }
    }

    private User getUserOrThrowBadRequest(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with id = " + userId));
    }

    private User getUserOrThrowNotFound(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + userId));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found with id = " + itemId));
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id = " + bookingId));
    }
}
