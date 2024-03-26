package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;

    @NotEmpty(message = "Item name can't be null.")
    private String name;

    @NotEmpty(message = "Item description can't be null.")
    private String description;

    @NotNull(message = "Item available field can't be null.")
    private Boolean available;

    private Long requestId;

    private BookingDtoItem lastBooking;

    private BookingDtoItem nextBooking;

    private List<CommentDto> comments;

}
