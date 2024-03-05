package ru.practicum.shareit.user.exception;

public class UserHasNotSavedException extends RuntimeException {

    public UserHasNotSavedException(String message) {
        super(message);
    }

}
