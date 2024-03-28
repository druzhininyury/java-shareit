package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.HasNotSavedException;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemService itemService;

    private ItemDto itemDtoIn;
    private ItemDto itemDtoOut;
    private ItemDto itemDtoUpdatedIn;
    private ItemDto itemDtoUpdatedOut;
    private CommentDto commentDtoIn;
    private CommentDto commentDtoOut;

    @BeforeEach
    void setUp() {
        itemDtoIn = ItemDto.builder()
                .name("item")
                .description("description")
                .available(true)
                .build();
        itemDtoOut = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .build();
        itemDtoUpdatedIn = ItemDto.builder()
                .name("updated")
                .build();
        itemDtoUpdatedOut = ItemDto.builder()
                .id(1L)
                .name("updated")
                .description("description")
                .available(true)
                .build();
        commentDtoIn = CommentDto.builder()
                .text("comment")
                .build();
        commentDtoOut = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("user")
                .created(LocalDateTime.of(2024, 03, 01, 12, 0))
                .build();
    }

    @Test
    void addItemTest() throws Exception {
        long userId = 1L;

        when(itemService.addItem(itemDtoIn, userId))
                .thenReturn(itemDtoOut);

        mvc.perform(post("/items")
                    .content(mapper.writeValueAsString(itemDtoIn))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .header("X-Sharer-User-Id", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));

        verify(itemService).addItem(itemDtoIn, userId);
    }

    @Test
    void addItem_NoSuchUserExceptionTest() throws Exception {
        long userId = 1L;

        when(itemService.addItem(any(ItemDto.class), anyLong())).thenThrow(new NoSuchEntityException("Error"));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addItem_NoSuchItemRequestExceptionTest() throws Exception {
        long userId = 1L;

        when(itemService.addItem(any(ItemDto.class), anyLong())).thenThrow(new NoSuchEntityException("Error"));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addItem_ItemHasNotSavedExceptionTest() throws Exception {
        long userId = 1L;

        when(itemService.addItem(any(ItemDto.class), anyLong())).thenThrow(new HasNotSavedException("Error"));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void updateItemDataTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.updateItemData(itemDtoUpdatedIn, itemId, userId))
                .thenReturn(itemDtoUpdatedOut);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDtoUpdatedIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoUpdatedOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoUpdatedOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpdatedOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoUpdatedOut.getAvailable())));

        verify(itemService).updateItemData(itemDtoUpdatedIn, itemId, userId);
    }

    @Test
    void updateItemData_NoSuchItemExceptionTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.updateItemData(any(ItemDto.class), anyLong(), anyLong()))
                .thenThrow(new NoSuchEntityException("Error"));

        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDtoUpdatedIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItemData_UserNotOwnItemExceptionTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.updateItemData(any(ItemDto.class), anyLong(), anyLong()))
                .thenThrow(new UserNotOwnItemException("Error"));

        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDtoUpdatedIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateItemData_ItemHasNotSavedExceptionTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.updateItemData(any(ItemDto.class), anyLong(), anyLong()))
                .thenThrow(new HasNotSavedException("Error"));

        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(itemDtoUpdatedIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void getItemByIdTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.getItemById(userId, itemId))
                .thenReturn(itemDtoOut);

        mvc.perform(get("/items/{itemId}", itemId)
                    .header("X-Sharer-User-Id", userId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOut.getAvailable())));

        verify(itemService).getItemById(userId, itemId);
    }

    @Test
    void getItemById_NoSuchUserExceptionTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.getItemById(anyLong(), anyLong())).thenThrow(new NoSuchEntityException("Error"));

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemById_NoSuchItemExceptionTest() throws Exception {
        long itemId = 1L;
        long userId = 1L;

        when(itemService.getItemById(anyLong(), anyLong())).thenThrow(new NoSuchEntityException("Error"));

        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemsByUserIdTest() throws Exception {
        long userId = 1L;
        long from = 0;
        long size = 10;

        when(itemService.getAllItemsByUserId(userId, from, size))
                .thenReturn(List.of(itemDtoOut));

        mvc.perform(get("/items?from={from}&size={size}", from, size)
                    .header("X-Sharer-User-Id", userId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoOut.getAvailable())));

        verify(itemService).getAllItemsByUserId(userId, from, size);
    }

    @Test
    void getAllItemsByUserId_NoSuchUserExceptionTest() throws Exception {
        long userId = 1L;
        long from = 0;
        long size = 10;

        when(itemService.getAllItemsByUserId(anyLong(), anyLong(), anyLong()))
                .thenThrow(new NoSuchEntityException("Error"));

        mvc.perform(get("/items?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllAvailableItemsWithTextTest() throws Exception {
        String searchString = "script";
        long from = 0;
        long size = 10;

        when(itemService.getAllItemsWithText(searchString, from, size))
                .thenReturn(List.of(itemDtoOut));

        mvc.perform(get("/items/search?text={text}&from={from}&size={size}", searchString, from, size)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoOut.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoOut.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoOut.getAvailable())));

        verify(itemService).getAllItemsWithText(searchString, from, size);
    }

    @Test
    void addCommentTest() throws Exception {
        long userId = 1;
        long itemId = 1;

        when(itemService.addComment(userId, itemId, commentDtoIn))
                .thenReturn(commentDtoOut);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                    .content(mapper.writeValueAsString(commentDtoIn))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .header("X-Sharer-User-Id", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoOut.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoOut.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDtoOut.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))));

        verify(itemService).addComment(userId, itemId, commentDtoIn);
    }

    @Test
    void addComment_NoSuchUserExceptionTest() throws Exception {
        long userId = 1;
        long itemId = 1;

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(new NoSuchEntityException("Error"));

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addComment_NoSuchItemExceptionTest() throws Exception {
        long userId = 1;
        long itemId = 1;

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(new NoSuchEntityException("Error"));

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addComment_NoFinishBookingForCommentExceptionTest() throws Exception {
        long userId = 1;
        long itemId = 1;

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(new NoFinishBookingForCommentException("Error"));

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_CommentHasNotSavedExceptionTest() throws Exception {
        long userId = 1;
        long itemId = 1;

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(new HasNotSavedException("Error"));

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}
