package ru.practicum.shareit.exception;

public class EmailAlreadyExistException extends RuntimeException {

    public EmailAlreadyExistException(String msg) {
        super(msg);
    }

}
