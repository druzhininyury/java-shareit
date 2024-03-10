package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.item.exception.*;
import ru.practicum.shareit.request.exception.ItemRequestHasNotSavedException;
import ru.practicum.shareit.request.exception.NoSuchItemRequestException;
import ru.practicum.shareit.user.exception.NoSuchUserException;
import ru.practicum.shareit.user.exception.UserHasNotSavedException;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoFinishBookingForCommentException(NoFinishBookingForCommentException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("No finished booking for comment.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidStateException(InvalidStateException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage(), "");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotBookingRelationException(NotBookingRelationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("User has no relation to booking.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNoWaitingStatusException(NoWaitingStatusException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Booking status is not waiting.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidStartEndDatesException(InvalidStartEndDatesException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Booking hasn't been saved.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemIsNotAvailableException(ItemIsNotAvailableException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Booking hasn't been saved.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleItemRequestHasNotSavedException(ItemRequestHasNotSavedException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("ItemRequest hasn't been saved.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleBookingHasNotSavedException(BookingHasNotSavedException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Booking hasn't been saved.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserHasNotSavedException(UserHasNotSavedException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("User hasn't been saved.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleCommentHasNotSavedException(CommentHasNotSavedException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Comment hasn't been saved.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchBookingException(NoSuchBookingException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("No such booking exists.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleItemHasNotSavedException(ItemHasNotSavedException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Item hasn't been saved.", e.getMessage());
    }

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
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchItemRequestException(NoSuchItemRequestException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("No such item request exists.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
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
