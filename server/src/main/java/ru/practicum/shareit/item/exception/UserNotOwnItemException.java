package ru.practicum.shareit.item.exception;

public class UserNotOwnItemException extends RuntimeException {

    public UserNotOwnItemException(String msg) {
        super(msg);
    }

}
