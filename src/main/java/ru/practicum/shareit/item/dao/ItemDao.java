package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {

    Item addItem(Item item);

    Item updateItemData(Item item);

    Item getItemById(long id);

    List<Item> getAllItemsByUserId(long userId);

    List<Item> getAllAvailableItemsWithText(String text);

    boolean isItemExists(long itemId);

    boolean isProperty(long itemId, long userId);

}
