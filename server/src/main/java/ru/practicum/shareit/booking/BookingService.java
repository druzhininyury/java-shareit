package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.List;

public interface BookingService {

    public enum State {
        ALL,
        CURRENT,
        PAST,
        FUTURE,
        WAITING,
        REJECTED
    }

    BookingDto addBooking(NewBookingDto newBookingDto, long userId);

    BookingDto approveOrRejectBooking(long bookingId, long userId, boolean approved);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByUser(long userId, String state, long from, long size);

    List<BookingDto> getAllBookingsAllItemsByOwner(long userId, String state, long from, long size);

}
