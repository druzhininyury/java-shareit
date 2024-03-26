package ru.practicum.shareit.request.exception;

public class ItemRequestHasNotSavedException extends RuntimeException {

    public ItemRequestHasNotSavedException(String message) {
        super(message);
    }
}
