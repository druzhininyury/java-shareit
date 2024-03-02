package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.exception.NoSuchItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemHasNotSavedException;
import ru.practicum.shareit.item.exception.UserNotOwnItemException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.NoSuchUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ItemDto addItem(@Valid ItemDto itemDto, long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("Can't add item, no user found with id=" + userId));
        Item item = ItemMapper.toItem(itemDto, owner);
        try {
            return ItemMapper.toItemDto(itemRepository.save(item));
        } catch(DataIntegrityViolationException e) {
            throw new ItemHasNotSavedException("Item hasn't been created: " + itemDto);
        }
    }

    @Override
    @Transactional
    public ItemDto updateItemData(ItemDto itemDto, long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchItemException("Item with id = " + itemId + " doesn't exist."));
        if (item.getOwner().getId() != userId) {
            throw new UserNotOwnItemException("User with id = " + userId + " doesn't own item with id = " + itemId);
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        try {
            return ItemMapper.toItemDto(itemRepository.save(item));
        } catch(DataIntegrityViolationException e) {
            throw new ItemHasNotSavedException("Item hasn't been updated: " + itemDto);
        }
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return ItemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchItemException("There is no item with id = " + itemId)));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId) {
        return ItemMapper.toItemDto(itemRepository.findAllByOwnerId(userId));
    }

    public List<ItemDto> getAllItemsWithText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return ItemMapper.toItemDto(itemRepository.findAllContainingText(text));
    }

}
