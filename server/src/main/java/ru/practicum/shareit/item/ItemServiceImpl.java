package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.HasNotSavedException;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchEntityException("Can't add item, no user found with id=" + userId));
        Item item = ItemMapper.toItem(itemDto, owner);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NoSuchEntityException("Can't add item, no item request with id = "
                            + itemDto.getRequestId()));
            item.setRequest(itemRequest);
        }
        try {
            return ItemMapper.toItemDto(itemRepository.save(item));
        } catch (DataIntegrityViolationException e) {
            throw new HasNotSavedException("Item hasn't been created: " + itemDto);
        }
    }

    @Override
    @Transactional
    public ItemDto updateItemData(ItemDto itemDto, long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchEntityException("Item with id = " + itemId + " doesn't exist."));
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
        } catch (DataIntegrityViolationException e) {
            throw new HasNotSavedException("Item hasn't been updated: " + itemDto);
        }
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchEntityException("There is no user with id = " + userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchEntityException("There is no item with id = " + itemId));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(CommentMapper.mapToCommentDto(commentRepository.findAllByItemId(itemId)));
        if (userId != item.getOwner().getId()) {
            return itemDto;
        }
        Optional<Booking> lastBooking =
                bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(
                        itemId, LocalDateTime.now(), Booking.Status.APPROVED);
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
    public List<ItemDto> getAllItemsByUserId(long userId, long from, long size) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchEntityException("There is no user with id = " + userId));
        PageRequest pageRequest = PageRequest.of((int) (from / size), (int) size);
        List<Item> items = itemRepository.findAllByOwnerId(userId, pageRequest);
        List<ItemDto> dtos = ItemMapper.toItemDto(items);
        for (ItemDto dto : dtos) {
            dto.setComments(CommentMapper.mapToCommentDto(commentRepository.findAllByItemId(dto.getId())));
            Optional<Booking> lastBooking =
                    bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(
                            dto.getId(), LocalDateTime.now(), Booking.Status.APPROVED);
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
        dtos.sort((left, right) -> {
            if (left.getId() > right.getId()) return 1;
            if (left.getId() < right.getId()) return -1;
            return 0; });
        return dtos;
    }

    @Override
    public List<ItemDto> getAllItemsWithText(String text, long from, long size) {
        if (text.isBlank()) {
            return List.of();
        }
        PageRequest pageRequest = PageRequest.of((int) (from / size), (int) size);
        return ItemMapper.toItemDto(itemRepository.findAllContainingText(text, pageRequest));
    }

    @Transactional
    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchEntityException("There is no user with id = " + userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchEntityException("There is no item with id = " + itemId));
        Booking booking = bookingRepository.findFirstByBookerIdAndItemIdAndStatusIsAndEndIsBeforeOrderByEndDesc(
                userId, itemId, Booking.Status.APPROVED, LocalDateTime.now()).orElseThrow(() ->
                new NoFinishBookingForCommentException("No booking for comment."));
        Comment comment = CommentMapper.mapToComment(commentDto, author, item, LocalDateTime.now());
        try {
            return CommentMapper.mapToCommentDto(commentRepository.save(comment));
        } catch (DataIntegrityViolationException e) {
            throw new HasNotSavedException("Comment hasn't been saved: " + comment);
        }
    }

}
