package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.ItemIsNotAvailableException;
import ru.practicum.shareit.item.exception.NoSuchItemException;
import ru.practicum.shareit.item.exception.UserNotOwnItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.NoSuchUserException;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto addBooking(@Valid NewBookingDto newBookingDto, long userId) {
        if (!isNewBookingDtoDatesValid(newBookingDto)) {
            throw new InvalidStartEndDatesException("End date is equal or less than start date.");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("There is no user with id = " + userId));
        Item item = itemRepository.findById(newBookingDto.getItemId()).orElseThrow(() ->
                new NoSuchItemException("There is no item with id = " + newBookingDto.getItemId()));
        if (item.getOwner().getId() == userId) {
            throw new NotBookingRelationException("User (id = " + userId + ") can't book item (id = "
                    + item.getId() + ") because he doesn't own it");
        }
        if (!item.getAvailable()) {
            throw new ItemIsNotAvailableException("Item with id = " + newBookingDto.getItemId() + " is not available.");
        }
        Booking booking = BookingMapper.mapToBooking(newBookingDto, user, item);
        try {
            return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
        } catch(DataIntegrityViolationException e) {
            throw new BookingHasNotSavedException("Booking hasn't been created: " + newBookingDto);
        }
    }

    @Transactional
    public BookingDto approveOrRejectBooking(long bookingId, long userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchBookingException("There is no booking with id = " + bookingId));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotBookingRelationException("User (id = " + userId + ") can't approve item (id = "
                    + booking.getItem().getId() + ") because not being owner.");
        }
        if (!booking.getStatus().equals(Booking.Status.WAITING)) {
            throw new NoWaitingStatusException("Can't approve/reject not waiting booking.");
        }
        booking.setStatus(approved ? Booking.Status.APPROVED : Booking.Status.REJECTED);
        try {
            return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
        } catch(DataIntegrityViolationException e) {
            throw new BookingHasNotSavedException("Booking (id = " + bookingId + ") hasn't been approve/rejected");
        }
    }

    public BookingDto getBookingById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchBookingException("There is no booking with id = " + bookingId));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotBookingRelationException("User (id = " + userId
                    + ") has no relation booker to booking (id = " + bookingId + ")");
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    public List<BookingDto> getAllBookingsByUser(long userId, String state) {
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("Unknown state: " + state);
        }
        User booker = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("There is no user with id = " + userId));
        List<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterAndEndIsBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Booking.Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Booking.Status.REJECTED);
                break;
            default:
                bookings = List.of();
        }
        return BookingMapper.mapToBookingDto(bookings);
    }

    public List<BookingDto> getAllBookingsAllItemsByOwner(long userId, String state) {
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("Unknown state: " + state);
        }
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("There is no user with id = " + userId));
        List<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Booking.Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Booking.Status.REJECTED);
                break;
            default:
                bookings = List.of();
        }
        return BookingMapper.mapToBookingDto(bookings);
    }

    private boolean isNewBookingDtoDatesValid(NewBookingDto newBookingDto) {
        if (newBookingDto.getStart().isEqual(newBookingDto.getEnd())
            || newBookingDto.getStart().isAfter(newBookingDto.getEnd())) {
            return false;
        }
        return true;
    }

}
