package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoIdName;
import ru.practicum.shareit.user.dto.UserDtoId;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    BookingService bookingService;

    @Test
    void addBookingTest() throws Exception {
        long itemId = 1L;
        long bookerId = 2L;
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(1L)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .status(Booking.Status.WAITING)
                .item(ItemDtoIdName.builder().id(itemId).name("item").build())
                .booker(UserDtoId.builder().id(bookerId).build())
                .build();

        when(bookingService.addBooking(newBookingDto, bookerId)).thenReturn(expectedBookingDto);

        mvc.perform(post("/bookings")
                    .content(mapper.writeValueAsString(newBookingDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .header("X-Sharer-User-Id", bookerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                    is(expectedBookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(expectedBookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(expectedBookingDto.getStatus().name())))
                .andExpect(jsonPath("$.item.id", is(expectedBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(expectedBookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(expectedBookingDto.getBooker().getId()), Long.class));

        verify(bookingService, times(1)).addBooking(newBookingDto, bookerId);
    }

    @Test
    void approveOrRejectBookingTest() throws Exception {
        long bookingId = 1L;
        long ownerId = 1L;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Booking.Status.APPROVED)
                .item(ItemDtoIdName.builder().id(1L).name("item").build())
                .booker(UserDtoId.builder().id(2L).build())
                .build();

        when(bookingService.approveOrRejectBooking(bookingId, ownerId, true)).thenReturn(expectedBookingDto);

        mvc.perform(patch("/bookings/{bookingId}?approved=true", bookingId)
                    .header("X-Sharer-User-Id", ownerId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(expectedBookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(expectedBookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(expectedBookingDto.getStatus().name())))
                .andExpect(jsonPath("$.item.id", is(expectedBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(expectedBookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(expectedBookingDto.getBooker().getId()), Long.class));

        verify(bookingService, times(1)).approveOrRejectBooking(bookingId, ownerId, true);
    }

    @Test
    void getBookingByIdTest() throws Exception {
        long bookingId = 1L;
        long ownerId = 1L;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Booking.Status.WAITING)
                .item(ItemDtoIdName.builder().id(1L).name("item").build())
                .booker(UserDtoId.builder().id(2L).build())
                .build();

        when(bookingService.getBookingById(bookingId, ownerId)).thenReturn(expectedBookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                    .header("X-Sharer-User-Id", ownerId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(expectedBookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.end",
                        is(expectedBookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.status", is(expectedBookingDto.getStatus().name())))
                .andExpect(jsonPath("$.item.id", is(expectedBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(expectedBookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(expectedBookingDto.getBooker().getId()), Long.class));

        verify(bookingService, times(1)).getBookingById(bookingId, ownerId);
    }

    @Test
    void getAllBookingsByCurrentUserTest() throws Exception {
        long bookerId = 2L;
        String state = "ALL";
        long from = 0;
        long size = 0;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Booking.Status.WAITING)
                .item(ItemDtoIdName.builder().id(1L).name("item").build())
                .booker(UserDtoId.builder().id(bookerId).build())
                .build();

        when(bookingService.getAllBookingsByUser(bookerId, state, from, size)).thenReturn(List.of(expectedBookingDto));

        mvc.perform(get("/bookings?state={state}&from={from}&size={size}", state, from, size)
                    .header("X-Sharer-User-Id", bookerId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(expectedBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start",
                        is(expectedBookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end",
                        is(expectedBookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].status", is(expectedBookingDto.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", is(expectedBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(expectedBookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(expectedBookingDto.getBooker().getId()), Long.class));

        verify(bookingService, times(1)).getAllBookingsByUser(bookerId, state, from, size);
    }

    @Test
    void getAllBookingsAllItemsByOwnerTest() throws Exception {
        long ownerId = 1L;
        String state = "ALL";
        long from = 0;
        long size = 0;
        BookingDto expectedBookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Booking.Status.WAITING)
                .item(ItemDtoIdName.builder().id(1L).name("item").build())
                .booker(UserDtoId.builder().id(2L).build())
                .build();

        when(bookingService.getAllBookingsAllItemsByOwner(ownerId, state, from, size))
                .thenReturn(List.of(expectedBookingDto));

        mvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", state, from, size)
                        .header("X-Sharer-User-Id", ownerId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(expectedBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start",
                        is(expectedBookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].end",
                        is(expectedBookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$[0].status", is(expectedBookingDto.getStatus().name())))
                .andExpect(jsonPath("$[0].item.id", is(expectedBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(expectedBookingDto.getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(expectedBookingDto.getBooker().getId()), Long.class));

        verify(bookingService, times(1)).getAllBookingsAllItemsByOwner(ownerId, state, from, size);
    }

}
