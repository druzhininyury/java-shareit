package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.NoSuchUserException;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto addBooking(NewBookingDto newBookingDto, long userId) {
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
        } catch (DataIntegrityViolationException e) {
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
        } catch (DataIntegrityViolationException e) {
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

    public List<BookingDto> getAllBookingsByUser(
            long userId, String state, long from, long size) {
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("Unknown state: " + state);
        }
        User booker = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("There is no user with id = " + userId));
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of((int) (from / size), (int) size, sortByStartDesc);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Booking.Status.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Booking.Status.REJECTED, pageRequest);
                break;
            default:
                bookings = List.of();
        }
        return BookingMapper.mapToBookingDto(bookings);
    }

    public List<BookingDto> getAllBookingsAllItemsByOwner(
            long userId, String state, long from, long size) {
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("Unknown state: " + state);
        }
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("There is no user with id = " + userId));
        List<Booking> bookings;
        Sort sortByStartDesc = Sort.by(Sort.Direction.DESC, "start");
        PageRequest pageRequest = PageRequest.of((int) (from / size), (int) size, sortByStartDesc);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerId(userId, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Booking.Status.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, Booking.Status.REJECTED, pageRequest);
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
