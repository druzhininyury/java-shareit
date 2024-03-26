package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequestsByOwner(long userId);

    List<ItemRequestDto> getItemRequestsAllButOwner(long userId, long from, long size);

    ItemRequestDto getItemRequestById(long userId, long requestId);

}
