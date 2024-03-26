package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewBookingDto {

    @NotNull(message = "No item id was provided for new booking.")
    private Long itemId;

    @NotNull(message = "No start date was provided for new booking.")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "No end date was provided for new booking.")
    @Future
    private LocalDateTime end;

}
