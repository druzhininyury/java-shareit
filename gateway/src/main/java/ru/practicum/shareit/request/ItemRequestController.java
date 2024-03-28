package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getItemRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsAllButOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @RequestParam(defaultValue = "0") long from,
                                                           @RequestParam(defaultValue = "10") long size) {
        return itemRequestClient.getItemRequestsAllButOwner(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

}
