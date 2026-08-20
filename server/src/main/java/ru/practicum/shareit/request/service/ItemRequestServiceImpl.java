package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
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
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto requestDto) {
        log.info("Creating item request for userId={}, requestDto={}", userId, requestDto);
        User requestor = getUserOrThrow(userId);
        ItemRequest request = ItemRequestMapper.toItem(requestDto, requestor, LocalDateTime.now());
        return ItemRequestMapper.toDto(itemRequestRepository.save(request), List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getOwnRequests(Long userId) {
        log.info("Getting own item requests for userId={}", userId);
        getUserOrThrow(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(userId);
        return toDtos(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId) {
        log.info("Getting foreign item requests for userId={}", userId);
        getUserOrThrow(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdNotOrderByCreatedDesc(userId);
        return toDtos(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getById(Long userId, Long requestId) {
        log.info("Getting item request by requestId={} for userId={}", requestId, userId);
        getUserOrThrow(userId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request not found"));
        List<ItemRequestItemDto> items = itemRepository.findByRequest_IdOrderByIdAsc(requestId).stream()
                .map(ItemRequestMapper::toItemDto)
                .toList();
        return ItemRequestMapper.toDto(request, items);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private List<ItemRequestDto> toDtos(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();
        Map<Long, List<ItemRequestItemDto>> itemsByRequestId = getItemsByRequestId(requestIds);

        return requests.stream()
                .map(request -> ItemRequestMapper.toDto(
                        request,
                        itemsByRequestId.getOrDefault(request.getId(), List.of())
                ))
                .toList();
    }

    private Map<Long, List<ItemRequestItemDto>> getItemsByRequestId(List<Long> requestIds) {
        if (requestIds.isEmpty()) {
            return Map.of();
        }
        return itemRepository.findByRequest_IdInOrderByIdAsc(requestIds).stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(ItemRequestMapper::toItemDto, Collectors.toList())
                ));
    }
}
