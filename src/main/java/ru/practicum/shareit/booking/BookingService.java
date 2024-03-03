package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import javax.validation.Valid;
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

    BookingDto addBooking(@Valid NewBookingDto newBookingDto, long userId);

    BookingDto approveOrRejectBooking(long bookingId, long userId, boolean approved);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByUser(long userId, String state);

    List<BookingDto> getAllBookingsAllItemsByOwner(long userId, String state);

}
