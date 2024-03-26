package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItemData(ItemDto itemDto, long itemId, long userId);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> getAllItemsByUserId(long userId, long from, long size);

    List<ItemDto> getAllItemsWithText(String text, long from, long size);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);

}
