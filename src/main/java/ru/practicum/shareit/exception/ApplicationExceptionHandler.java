package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExistException(EmailAlreadyExistException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Email already exists.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Provided email is invalid.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchUserException(NoSuchUserException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("No such user exists.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoUserIdProvidedException(NoUserIdProvidedException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("No user id provided.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoSuchItemException(NoSuchItemException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("No such item exists.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleUserNotOwnItemException(UserNotOwnItemException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("User doesn't own item.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Internal server error.", e.getMessage());
    }

}
