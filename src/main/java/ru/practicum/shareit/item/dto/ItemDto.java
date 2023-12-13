package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
public class ItemDto {

    private long id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private Long requestId;

}
