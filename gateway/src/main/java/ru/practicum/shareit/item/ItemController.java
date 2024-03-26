package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItemData(@RequestBody ItemDto itemDto,
                                  @PathVariable long itemId,
                                  @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.updateItemData(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(defaultValue = "0") long from,
                                             @RequestParam(defaultValue = "10") long size) {
        return itemClient.getAllItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAllAvailableItemsWithText(@RequestParam String text,
                                                      @RequestParam(defaultValue = "0") long from,
                                                      @RequestParam(defaultValue = "10") long size) {
        return itemClient.getAllItemsWithText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }

}
