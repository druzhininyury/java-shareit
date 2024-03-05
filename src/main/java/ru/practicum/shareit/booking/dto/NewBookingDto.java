package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class NewBookingDto {

    @NotNull(message = "No item id was provided for new booking.")
    private long itemId;

    @NotNull(message = "No start date was provided for new booking.")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "No end date was provided for new booking.")
    @Future
    private LocalDateTime end;

}
