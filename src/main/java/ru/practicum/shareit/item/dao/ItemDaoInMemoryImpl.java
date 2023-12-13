package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemDaoInMemoryImpl implements ItemDao {

    private long nextId = 1;
    private Map<Long, Item> itemsById = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        item.setId(getNextId());
        item.setAvailable(true);
        itemsById.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItemData(Item item) {
        Item inMemoryItem = itemsById.get(item.getId());
        if (item.getName() != null) {
            inMemoryItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            inMemoryItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            inMemoryItem.setAvailable(item.getAvailable());
        }
        return inMemoryItem;
    }

    @Override
    public Item getItemById(long itemId) {
        return itemsById.get(itemId);
    }

    @Override
    public List<Item> getAllItemsByUserId(long userId) {
        List<Item> result = new ArrayList<>();
        for (Item item : itemsById.values()) {
            if (item.getOwnerId() == userId) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public List<Item> getAllAvailableItemsWithText(String text) {
        List<Item> result = new ArrayList<>();
        if (text.isBlank()) {
            return result;
        }
        text = text.toLowerCase();
        for (Item item : itemsById.values()) {
            if ((item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text))
                    && item.getAvailable() == true) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public boolean isItemExists(long itemId) {
        return itemsById.containsKey(itemId);
    }

    @Override
    public boolean isProperty(long itemId, long userId) {
        return itemsById.get(itemId).getOwnerId() == userId;
    }

    private long getNextId() {
        return nextId++;
    }

}
