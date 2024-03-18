package ru.practicum.shareit.request.exception;

public class NoSuchItemRequestException extends RuntimeException {

    public NoSuchItemRequestException(String message) {
        super(message);
    }
}
