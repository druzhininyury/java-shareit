package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(itemRequestDto.getCreated());
        itemRequest.setRequester(requester);
        return itemRequest;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequester(itemRequest.getRequester().getId());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(new ArrayList<>());
        return itemRequestDto;
    }

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, List<ItemDto> items) {
        ItemRequestDto itemRequestDto = mapToItemRequestDto(itemRequest);
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

}
