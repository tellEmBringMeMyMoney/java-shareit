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
        log.info("Getting item={} in service", itemId);
        Item item = getItemOrThrow(itemId);

        List<CommentDto> comments = commentRepository.findByItem_IdInOrderByCreatedAsc(List.of(itemId))
                .stream()
                .map(CommentMapper::toCommentDto)
                .toList();

        List<BookingDto> bookings = bookingRepository.findApprovedByItemInOrderByStartAsc(List.of(item))
                .stream()
                .map(BookingMapper::toBookingDto)
                .toList();

        return toDetailedItemDto(item, userId, comments, bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getOwnerItems(Long userId) {
        log.info("Getting items by owner={}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }

        List<Item> items = itemRepository.findByOwner_IdOrderByIdAsc(userId);
        if (items.isEmpty()) {
            return List.of();
        }

        Map<Long, List<BookingDto>> bookingsByItemId = bookingRepository.findApprovedByItemInOrderByStartAsc(items)
                .stream()
                .collect(Collectors.groupingBy(
                        booking -> booking.getItem().getId(),
                        Collectors.mapping(BookingMapper::toBookingDto, Collectors.toList())
                ));

        List<Long> itemIds = items.stream().map(Item::getId).toList();

        Map<Long, List<CommentDto>> commentsByItemId = commentRepository.findByItem_IdInOrderByCreatedAsc(itemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toCommentDto, Collectors.toList())
                ));

        return items.stream()
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

        boolean hasCompletedBooking = bookingRepository.existsByItem_IdAndBooker_IdAndStatusAndEndBefore(
                itemId,
                userId,
                Status.APPROVED,
                LocalDateTime.now()
        );
        if (!hasCompletedBooking) {
            throw new BadRequestException("Commenting allowed only with approved booking");
        }

        User author = userRepository.getReferenceById(userId);
        Item item = itemRepository.getReferenceById(itemId);

        Comment comment = CommentMapper.toComment(commentDto, item, author, LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(savedComment);
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

    private ItemDto toDetailedItemDto(Item item, Long userId, List<CommentDto> comments, List<BookingDto> bookings) {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(comments);
        if (item.getOwner() != null && item.getOwner().getId().equals(userId) && !bookings.isEmpty()) {
            itemDto.setNextBooking(BookingMapper.toBookingDtoShort(bookings.getFirst()));
            itemDto.setLastBooking(BookingMapper.toBookingDtoShort(bookings.getLast()));
        }
        return itemDto;
    }
}
