package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ItemDto {

    private Long id;

    @NotEmpty(message = "Item name can't be null.")
    private String name;

    @NotEmpty(message = "Item description can't be null.")
    private String description;

    @NotNull(message = "Item available field can't be null.")
    private Boolean available;

    //private Long ownerId;
    //private Long requestId;

}
