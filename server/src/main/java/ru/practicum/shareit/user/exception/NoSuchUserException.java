package ru.practicum.shareit.user.exception;

public class NoSuchUserException extends RuntimeException {

    public NoSuchUserException(String msg) {
        super(msg);
    }

}
