package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.item.exception.*;

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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Provided email is invalid.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleUserNotOwnItemException(UserNotOwnItemException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("User doesn't own item.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleHasNotSavedException(HasNotSavedException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Entity hasn't been saved.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchEntityException(NoSuchEntityException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("No such entity exists.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Internal server error.", e.getMessage());
    }

}
