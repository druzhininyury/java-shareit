package ru.practicum.shareit.booking.exception;

public class InvalidStateException extends RuntimeException {

    public InvalidStateException(String message) {
        super(message);
    }

}
