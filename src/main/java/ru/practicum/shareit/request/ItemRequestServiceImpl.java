package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.exception.ItemRequestHasNotSavedException;
import ru.practicum.shareit.request.exception.NoSuchItemRequestException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.NoSuchUserException;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto addItemRequest(long userId, @Valid ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("There is no user with id = " + userId));
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto, user);
        try {
            return ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.save(itemRequest), List.of());
        } catch (DataIntegrityViolationException e) {
            throw new ItemRequestHasNotSavedException("Item hasn't been created: " + itemRequestDto);
        }
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByOwner(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("There is no user with id = " + userId));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        List<Long> itemRequestIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<ItemRequestDto> itemRequestDtos = itemRequests.stream().map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdIn(itemRequestIds);
        for (ItemRequestDto itemRequestDto : itemRequestDtos) {
            List<Item> requestItems = items.stream()
                    .filter(item -> item.getRequest().getId() == itemRequestDto.getId())
                    .collect(Collectors.toList());
            itemRequestDto.setItems(ItemMapper.toItemDto(requestItems));
        }
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getItemRequestsAllButOwner(long userId, @PositiveOrZero long from, @Positive long size) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("There is no user with id = " + userId));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdIsNot(userId ,
                PageRequest.of((int) (from / size), (int) size, Sort.by(Sort.Direction.DESC, "created")));
        List<Long> itemRequestIds = itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<ItemRequestDto> itemRequestDtos = itemRequests.stream().map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdIn(itemRequestIds);
        for (ItemRequestDto itemRequestDto : itemRequestDtos) {
            List<Item> requestItems = items.stream()
                    .filter(item -> item.getRequest().getId() == itemRequestDto.getId())
                    .collect(Collectors.toList());
            itemRequestDto.setItems(ItemMapper.toItemDto(requestItems));
        }
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getItemRequestById(long userId, long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("There is no user with id = " + userId));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NoSuchItemRequestException("There is no item request with id = " + requestId));
        List<ItemDto> itemDtos = ItemMapper.toItemDto(itemRepository.findAllByRequestId(requestId));
        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, itemDtos);
        return itemRequestDto;
    }
}
