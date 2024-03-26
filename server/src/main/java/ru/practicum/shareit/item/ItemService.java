package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

public interface ItemService {

    ItemDto addItem(@Valid ItemDto itemDto, long userId);

    ItemDto updateItemData(ItemDto itemDto, long itemId, long userId);

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> getAllItemsByUserId(long userId, @PositiveOrZero long from, @Positive long size);

    List<ItemDto> getAllItemsWithText(String text, @PositiveOrZero long from, @Positive long size);

    CommentDto addComment(long userId, long itemId, @Valid CommentDto commentDto);

}
