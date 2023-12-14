package ru.practicum.shareit.exception;

public class NoEmailProvidedException extends RuntimeException {

    public NoEmailProvidedException(String msg) {
        super(msg);
    }

}
