package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemMapperTest {

    @Test
    void toItemDtoTest() {
        User owner = User.builder().id(1L).name("owner").email("owner@yandex.ru").build();
        Item item1 = Item.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .owner(owner)
                .build();
        LocalDateTime creationTime = LocalDateTime.now();
        Item item2 = Item.builder()
                .id(2L)
                .name("item2")
                .description("description2")
                .available(true)
                .owner(owner)
                .request(ItemRequest.builder()
                        .id(1L)
                        .description("description")
                        .requester(User.builder()
                                .id(2L)
                                .name("requester")
                                .email("requestor@yandex.ru")
                                .build())
                        .created(creationTime)
                        .build())
                .build();

        ItemDto expectedItemDto1 = ItemDto.builder()
                .id(item1.getId())
                .name(item1.getName())
                .description(item1.getDescription())
                .available(item1.getAvailable())
                .build();
        ItemDto expectedItemDto2 = ItemDto.builder()
                .id(item2.getId())
                .name(item2.getName())
                .description(item2.getDescription())
                .available(item2.getAvailable())
                .requestId(item2.getRequest().getId())
                .build();

        ItemDto actualItemDto1 = ItemMapper.toItemDto(item1);
        ItemDto actualItemDto2 = ItemMapper.toItemDto(item2);
        List<ItemDto> actualList = ItemMapper.toItemDto(List.of(item1, item2));

        assertThat(actualItemDto1, equalTo(expectedItemDto1));
        assertThat(actualItemDto2, equalTo(expectedItemDto2));
        assertThat(actualList, equalTo(List.of(expectedItemDto1, expectedItemDto2)));
    }

    @Test
    void toItemTest() {
        ItemDto itemDto1 = ItemDto.builder()
                .id(1L)
                .name("item1")
                .description("description1")
                .available(true)
                .build();
        ItemDto itemDto2 = ItemDto.builder()
                .name("item2")
                .description("description2")
                .available(true)
                .build();
        User owner = User.builder().id(1L).name("owner").email("owner@yandex.ru").build();

        Item expectedItem1 = Item.builder()
                .id(itemDto1.getId())
                .name(itemDto1.getName())
                .description(itemDto1.getDescription())
                .available(itemDto1.getAvailable())
                .build();
        Item expectedItem2 = Item.builder()
                .id(0)
                .name(itemDto2.getName())
                .description(itemDto2.getDescription())
                .available(itemDto2.getAvailable())
                .build();
        Item expectedItem3 = Item.builder()
                .id(itemDto1.getId())
                .name(itemDto1.getName())
                .description(itemDto1.getDescription())
                .available(itemDto1.getAvailable())
                .owner(owner)
                .build();

        Item actualItem1 = ItemMapper.toItem(itemDto1);
        Item actualItem2 = ItemMapper.toItem(itemDto2);
        Item actualItem3 = ItemMapper.toItem(itemDto1, owner);

        assertThat(actualItem1.getId(), equalTo(expectedItem1.getId()));
        assertThat(actualItem1.getName(), equalTo(expectedItem1.getName()));
        assertThat(actualItem1.getDescription(), equalTo(expectedItem1.getDescription()));
        assertThat(actualItem1.getAvailable(), equalTo(expectedItem1.getAvailable()));

        assertThat(actualItem2.getId(), equalTo(expectedItem2.getId()));
        assertThat(actualItem2.getName(), equalTo(expectedItem2.getName()));
        assertThat(actualItem2.getDescription(), equalTo(expectedItem2.getDescription()));
        assertThat(actualItem2.getAvailable(), equalTo(expectedItem2.getAvailable()));

        assertThat(actualItem3.getId(), equalTo(expectedItem3.getId()));
        assertThat(actualItem3.getName(), equalTo(expectedItem3.getName()));
        assertThat(actualItem3.getDescription(), equalTo(expectedItem3.getDescription()));
        assertThat(actualItem3.getAvailable(), equalTo(expectedItem3.getAvailable()));
        assertThat(actualItem3.getOwner().getId(), equalTo(expectedItem3.getOwner().getId()));
    }

}
