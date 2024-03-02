package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        //itemDto.setOwnerId(item.getOwnerId());
        //itemDto.setRequestId(item.getRequestId());
        return itemDto;
    }

    public static List<ItemDto> toItemDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        if (itemDto.getId() != null) {
            item.setId(itemDto.getId());
        }
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        //item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        Item item = toItem(itemDto);
        item.setOwner(owner);
        return item;
    }

}
