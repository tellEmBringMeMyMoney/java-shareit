package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("Created item by user={}, itemDto={}", userId, itemDto);
        User owner = getUserOrThrow(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.create(item));
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

        return ItemMapper.toItemDto(itemRepository.update(existingItem));
    }

    @Override
    public ItemDto getById(Long itemId) {
        log.info("Getting item={} in service", itemId);
        return ItemMapper.toItemDto(getItemOrThrow(itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long userId) {
        log.info("Getting items by owner={}", userId);
        getUserOrThrow(userId);
        return itemRepository.findByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
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
}
