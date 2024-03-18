package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(long userId, @Valid ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequestsByOwner(long userId);

    List<ItemRequestDto> getItemRequestsAllButOwner(long userId, @PositiveOrZero long from, @Positive long size);

    ItemRequestDto getItemRequestById(long userId, long requestId);

}
