package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoIdName;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoId;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingMapperTest {

    @Test
    void mapToBookingDtoTest() {
        User owner = User.builder().id(1L).name("owner").email("owner@yandex.ru").build();
        User booker = User.builder().id(2L).name("booker").email("booker@yandex.ru").build();
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Booking.Status.WAITING)
                .build();

        BookingDto expectedBookingDto = BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDtoIdName.builder().id(item.getId()).name(item.getName()).build())
                .booker(UserDtoId.builder().id(booker.getId()).build())
                .build();

        BookingDto actualBookingDto = BookingMapper.mapToBookingDto(booking);
        List<BookingDto> actualList = BookingMapper.mapToBookingDto(List.of(booking));

        assertThat(actualBookingDto, equalTo(expectedBookingDto));
        assertThat(actualList, equalTo(List.of(expectedBookingDto)));
    }

    @Test
    void mapToBookingTest() {
        User owner = User.builder().id(1L).name("owner").email("owner@yandex.ru").build();
        User booker = User.builder().id(2L).name("booker").email("booker@yandex.ru").build();
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Booking expectedBooking = Booking.builder()
                .id(0L)
                .start(newBookingDto.getStart())
                .end(newBookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(Booking.Status.WAITING)
                .build();

        Booking actualBooking = BookingMapper.mapToBooking(newBookingDto, booker, item);

        assertThat(actualBooking.getId(), equalTo(expectedBooking.getId()));
        assertThat(actualBooking.getStart(), equalTo(expectedBooking.getStart()));
        assertThat(actualBooking.getEnd(), equalTo(expectedBooking.getEnd()));
        assertThat(actualBooking.getItem().getId(), equalTo(expectedBooking.getItem().getId()));
        assertThat(actualBooking.getBooker().getId(), equalTo(expectedBooking.getBooker().getId()));
        assertThat(actualBooking.getStatus(), equalTo(expectedBooking.getStatus()));

    }

    @Test
    void mapToBookingDtoItemTest() {
        User owner = User.builder().id(1L).name("owner").email("owner@yandex.ru").build();
        User booker = User.builder().id(2L).name("booker").email("booker@yandex.ru").build();
        Item item = Item.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(Booking.Status.WAITING)
                .build();

        BookingDtoItem expectedBookingDtoItem = BookingDtoItem.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();

        BookingDtoItem actualBookingDtoItem = BookingMapper.mapToBookingDtoItem(booking);

        assertThat(actualBookingDtoItem, equalTo(expectedBookingDtoItem));
    }
}
