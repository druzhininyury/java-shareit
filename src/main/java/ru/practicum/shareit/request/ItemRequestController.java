package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    List<ItemRequestDto> getItemRequestsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getItemRequestsByOwner(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getItemRequestsAllButOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "0") long from,
                                                    @RequestParam(defaultValue = "10") long size) {
        return itemRequestService.getItemRequestsAllButOwner(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }

}
