package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("Created item by user={}, itemDto={}", userId, itemDto);
        User owner = getUserOrThrow(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("ItemRequest not found with id: " + itemDto.getRequestId()));
            item.setRequest(request);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Updated itemId={}, by user={}, itemDto={}", itemId, userId, itemDto);
        getUserOrThrow(userId);
        Item existingItem = getItemOrThrow(itemId);
        validateOwner(existingItem, userId);

        if (validateName(itemDto.getName())) {
            existingItem.setName(itemDto.getName());
        }
        if (validateDescription(itemDto.getDescription())) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long itemId) {
        return getById(itemId, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long itemId, Long userId) {
        log.info("Getting item in service by itemId={}", itemId);
        Item item = getItemOrThrow(itemId);
        Map<Long, List<CommentDto>> commentsByItemId = getCommentsByItemId(List.of(item));

        Map<Long, List<BookingDto>> bookingsByItemId = getBookingsByItemId(List.of(item));
        return toDetailedItemDto(
                item,
                userId,
                commentsByItemId.getOrDefault(item.getId(), List.of()),
                bookingsByItemId.getOrDefault(item.getId(), List.of())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getOwnerItems(Long userId) {
        log.info("Getting owner items in service for userId={}", userId);
        getUserOrThrow(userId);
        List<Item> items = itemRepository.findByOwner_IdOrderByIdAsc(userId);
        Map<Long, List<BookingDto>> bookingsByItemId = getBookingsByItemId(items);
        Map<Long, List<CommentDto>> commentsByItemId = getCommentsByItemId(items);

        return items
                .stream()
                .map(item -> toDetailedItemDto(
                        item,
                        userId,
                        commentsByItemId.getOrDefault(item.getId(), List.of()),
                        bookingsByItemId.getOrDefault(item.getId(), List.of())
                ))
                .toList();
    }


    @Override
    public List<ItemDto> search(String text) {
        log.info("Searching item contains text ={}", text);
        if (text == null || text.isBlank()) {
            log.info("Search request is empty, nothing to return");
            return List.of();
        }
        return itemRepository.search(text.trim())
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Adding comment for itemId={}, userId={}, dto={}", itemId, userId, commentDto);
        User author = getUserOrThrow(userId);
        Item item = getItemOrThrow(itemId);
        validateComment(commentDto);

        boolean hasBooking = bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                userId,
                itemId,
                Status.APPROVED,
                LocalDateTime.now()
        );

        if (!hasBooking) {
            throw new BadRequestException("User cannot leave a comment for this item");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, author, LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("UserId={} not found", userId);
                    return new NotFoundException("User not found");
                });
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("ItemId={} not found", itemId);
                    return new NotFoundException("Item not found");
                });
    }

    private void validateOwner(Item item, Long userId) {
        if (!item.getOwner().getId().equals(userId)) {
            log.warn("UserId={} is not owner of itemId={}", userId, item.getId());
            throw new ForbiddenException("Item can be updated only by owner");
        }
    }

    private boolean validateName(String name) {
        return name != null && !name.isBlank();
    }

    private boolean validateDescription(String description) {
        return description != null && !description.isBlank();
    }

    private void validateComment(CommentDto commentDto) {
        if (commentDto == null || commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new BadRequestException("Comment text can not be empty");
        }
    }

    private ItemDto toDetailedItemDto(Item item, Long userId, List<CommentDto> comments, List<BookingDto> bookings) {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(comments);

        if (item.getOwner() != null && item.getOwner().getId().equals(userId) && !bookings.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();

            BookingDto lastBooking = bookings.stream()
                    .filter(b -> b.getStatus() != Status.REJECTED)
                    .filter(b -> !b.getStart().isAfter(now))
                    .reduce((first, second) -> second)
                    .orElse(null);

            BookingDto nextBooking = bookings.stream()
                    .filter(b -> b.getStatus() != Status.REJECTED)
                    .filter(b -> b.getStart().isAfter(now))
                    .findFirst()
                    .orElse(null);

            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingDtoShort(lastBooking));
            }
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingDtoShort(nextBooking));
            }
        }
        return itemDto;
    }

    @Transactional(readOnly = true)
    private Map<Long, List<BookingDto>> getBookingsByItemId(List<Item> items) {
        return bookingRepository.findApprovedByItemInOrderByStartAsc(items)
                .stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getItem().getId(),
                        Collectors.mapping(BookingMapper::toBookingDto, Collectors.toList())
                ));
    }

    @Transactional(readOnly = true)
    private Map<Long, List<CommentDto>> getCommentsByItemId(List<Item> items) {
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();
        if (itemIds.isEmpty()) {
            return Map.of();
        }
        return commentRepository.findByItem_IdInOrderByCreatedAsc(itemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())
                ));
    }
}
