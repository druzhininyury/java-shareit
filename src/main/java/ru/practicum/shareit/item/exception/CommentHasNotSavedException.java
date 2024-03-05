package ru.practicum.shareit.item.exception;

public class CommentHasNotSavedException extends RuntimeException {

    public CommentHasNotSavedException(String message) {
        super(message);
    }
}
