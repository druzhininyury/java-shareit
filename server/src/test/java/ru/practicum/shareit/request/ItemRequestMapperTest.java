package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemRequestMapperTest {

    @Test
    void mapToItemRequestDtoTest() {
        User requester = User.builder().id(2L).name("requester").email("requester@yandex.ru").build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("request")
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        ItemRequestDto expectedItemRequestDto1 = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester().getId())
                .created(itemRequest.getCreated())
                .items(List.of())
                .build();
        ItemRequestDto expectedItemRequestDto2 = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester().getId())
                .created(itemRequest.getCreated())
                .items(List.of(itemDto))
                .build();

        ItemRequestDto actualItemRequestDto1 = ItemRequestMapper.mapToItemRequestDto(itemRequest);
        ItemRequestDto actualItemRequestDto2 = ItemRequestMapper.mapToItemRequestDto(itemRequest, List.of(itemDto));

        assertThat(actualItemRequestDto1, equalTo(expectedItemRequestDto1));
        assertThat(actualItemRequestDto2, equalTo(expectedItemRequestDto2));
    }

    @Test
    void mapToItemRequestTest() {
        User requester = User.builder().id(2L).name("requester").email("requester@yandex.ru").build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("request")
                .created(LocalDateTime.now())
                .build();

        ItemRequest expectedItemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(requester)
                .created(itemRequestDto.getCreated())
                .build();

        ItemRequest actualItemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto, requester);

        assertThat(actualItemRequest.getId(), equalTo(expectedItemRequest.getId()));
        assertThat(actualItemRequest.getDescription(), equalTo(expectedItemRequest.getDescription()));
        assertThat(actualItemRequest.getRequester().getId(), equalTo(expectedItemRequest.getRequester().getId()));
        assertThat(actualItemRequest.getCreated(), equalTo(expectedItemRequest.getCreated()));
    }
}
