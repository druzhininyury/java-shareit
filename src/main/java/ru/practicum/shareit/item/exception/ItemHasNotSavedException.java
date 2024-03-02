package ru.practicum.shareit.item.exception;

public class ItemHasNotSavedException extends RuntimeException {

    public ItemHasNotSavedException(String message) {
        super(message);
    }
}
