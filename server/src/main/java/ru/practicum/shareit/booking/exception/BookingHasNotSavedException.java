package ru.practicum.shareit.booking.exception;

public class BookingHasNotSavedException extends RuntimeException {

    public BookingHasNotSavedException(String message) {
        super(message);
    }

}
