package ru.practicum.shareit.booking.dto;


import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoIdName;
import ru.practicum.shareit.user.dto.UserDtoId;

import java.time.LocalDateTime;

@Data
public class BookingDto {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Booking.Status status;

    private ItemDtoIdName item;

    private UserDtoId booker;
}
