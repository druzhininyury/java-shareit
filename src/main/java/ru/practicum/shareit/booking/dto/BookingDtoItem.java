package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDtoItem {

    private long id;

    private long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;
}
