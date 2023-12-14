package ru.practicum.shareit.exception;

public class NoSuchUserException extends RuntimeException {

    public NoSuchUserException(String msg) {
        super(msg);
    }

}
