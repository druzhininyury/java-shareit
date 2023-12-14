package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NoSuchItemException;
import ru.practicum.shareit.exception.NoSuchUserException;
import ru.practicum.shareit.exception.NoUserIdProvidedException;
import ru.practicum.shareit.exception.UserNotOwnItemException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated
public class ItemService {

    private ItemDao itemDao;
    private UserDao userDao;

    @Autowired
    public ItemService(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    public Item addItem(@Valid Item item, Long userId) {
        if (userId == null) {
            throw new NoUserIdProvidedException("Can't add item, no user id was provided.");
        }
        if (!userDao.isUserExists(userId)) {
            throw new NoSuchUserException("Can't add item, no user found with id=" + userId);
        }
        item.setOwnerId(userId);
        return itemDao.addItem(item);
    }

    public Item updateItemData(Item item, Long userId) {
        if (userId == null) {
            throw new NoUserIdProvidedException("Can't update item, no user id was provided.");
        }
        if (!userDao.isUserExists(userId)) {
            throw new NoSuchUserException("Can't update item, no user found with id=" + userId);
        }
        if (!itemDao.isItemExists(item.getId())) {
            throw new NoSuchItemException("Can't update item, no item found with id=" + item.getId());
        }
        if (!itemDao.isProperty(item.getId(), userId)) {
            throw new UserNotOwnItemException("Can't update item, user with id=" + userId +
                    " doesn't own item with id=" + item.getId());
        }
        item.setOwnerId(userId);
        return itemDao.updateItemData(item);
    }

    public Item getItemById(long itemId) {
        return itemDao.getItemById(itemId);
    }

    public List<Item> getAllItemsByUserId(Long userId) {
        if (userId == null) {
            throw new NoUserIdProvidedException("Can't return items, no user id was provided.");
        }
        if (!userDao.isUserExists(userId)) {
            throw new NoSuchUserException("Can't return items, no user found with id=" + userId);
        }
        return itemDao.getAllItemsByUserId(userId);
    }

    public List<Item> getAllAvailableItemsWithText(String text) {
        return itemDao.getAllAvailableItemsWithText(text);
    }

}
