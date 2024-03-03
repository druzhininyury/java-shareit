package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

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
    public ItemDto getItemById(long userId, long itemId) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("There is no user with id = " + userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchItemException("There is no item with id = " + itemId));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (userId != item.getOwner().getId()) {
            return itemDto;
        }
        Optional<Booking> lastBooking =
                bookingRepository.findFirstByItemIdAndEndIsBeforeAndStatusOrderByEndDesc
                        (itemId, LocalDateTime.now(), Booking.Status.APPROVED);
        if (lastBooking.isPresent()) {
            itemDto.setLastBooking(BookingMapper.mapToBookingDtoItem(lastBooking.get()));
        }
        Optional<Booking> nextBooking =
                bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(
                        itemId, LocalDateTime.now(), Booking.Status.APPROVED);
        if (nextBooking.isPresent()) {
            itemDto.setNextBooking(BookingMapper.mapToBookingDtoItem(nextBooking.get()));
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("There is no user with id = " + userId));
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDto> dtos = ItemMapper.toItemDto(items);
        for (ItemDto dto : dtos) {
            Optional<Booking> lastBooking =
                    bookingRepository.findFirstByItemIdAndEndIsBeforeAndStatusOrderByEndDesc
                            (dto.getId(), LocalDateTime.now(), Booking.Status.APPROVED);
            if (lastBooking.isPresent()) {
                dto.setLastBooking(BookingMapper.mapToBookingDtoItem(lastBooking.get()));
            }
            Optional<Booking> nextBooking =
                    bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(
                            dto.getId(), LocalDateTime.now(), Booking.Status.APPROVED);
            if (nextBooking.isPresent()) {
                dto.setNextBooking(BookingMapper.mapToBookingDtoItem(nextBooking.get()));
            }
        }
        return dtos;
    }

    public List<ItemDto> getAllItemsWithText(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return ItemMapper.toItemDto(itemRepository.findAllContainingText(text));
    }

}
