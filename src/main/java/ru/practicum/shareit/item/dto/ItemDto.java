package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ItemDto {

    private Long id;

    @NotEmpty(message = "Item name can't be null.")
    private String name;

    @NotEmpty(message = "Item description can't be null.")
    private String description;

    @NotNull(message = "Item available field can't be null.")
    private Boolean available;

    private BookingDtoItem lastBooking;

    private BookingDtoItem nextBooking;

    private List<CommentDto> comments;

}
