package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemRequestService itemRequestService;

    @Test
    void addItemRequestTest() throws Exception {
        long requesterId = 2L;
        ItemRequestDto newItemRequestDto = ItemRequestDto.builder()
                .description("description")
                .requester(requesterId)
                .build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requester(requesterId)
                .items(List.of())
                .build();

        when(itemRequestService.addItemRequest(requesterId, newItemRequestDto)).thenReturn(expectedItemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(newItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", requesterId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(expectedItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester", is(expectedItemRequestDto.getRequester()), Long.class))
                .andExpect(jsonPath("$.items", hasSize(0)));

        verify(itemRequestService).addItemRequest(requesterId, newItemRequestDto);
    }

    @Test
    void getItemRequestsByOwnerTest() throws Exception {
        long requesterId = 2L;
        long requestId = 1L;
        long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(requestId)
                .description("description")
                .requester(requesterId)
                .items(List.of(itemDto))
                .build();

        when(itemRequestService.getItemRequestsByOwner(requesterId)).thenReturn(List.of(expectedItemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requesterId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expectedItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(expectedItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requester", is(expectedItemRequestDto.getRequester()), Long.class))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemDto.getRequestId()), Long.class));

        verify(itemRequestService).getItemRequestsByOwner(requesterId);
    }

    @Test
    void getItemRequestsAllButOwnerTest() throws Exception {
        long requesterId = 2L;
        long requestId = 2L;
        long itemId = 2L;
        long from = 0;
        long size = 10;
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(requestId)
                .description("description")
                .requester(requesterId)
                .items(List.of(itemDto))
                .build();

        when(itemRequestService.getItemRequestsAllButOwner(requesterId, from, size))
                .thenReturn(List.of(expectedItemRequestDto));

        mvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", requesterId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expectedItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(expectedItemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].requester", is(expectedItemRequestDto.getRequester()), Long.class))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemDto.getRequestId()), Long.class));

        verify(itemRequestService).getItemRequestsAllButOwner(requesterId, from, size);
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        long requesterId = 2L;
        long requestId = 1L;
        long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        ItemRequestDto expectedItemRequestDto = ItemRequestDto.builder()
                .id(requestId)
                .description("description")
                .requester(requesterId)
                .items(List.of(itemDto))
                .build();

        when(itemRequestService.getItemRequestById(requesterId, requestId)).thenReturn(expectedItemRequestDto);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", requesterId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(expectedItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester", is(expectedItemRequestDto.getRequester()), Long.class))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(itemDto.getRequestId()), Long.class));

        verify(itemRequestService).getItemRequestById(requesterId, requestId);
    }
}
