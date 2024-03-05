package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoIdName;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoId;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(new ItemDtoIdName(booking.getItem().getId(), booking.getItem().getName()));
        bookingDto.setBooker(new UserDtoId(booking.getBooker().getId()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static List<BookingDto> mapToBookingDto(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::mapToBookingDto).collect(Collectors.toList());
    }

    public static Booking mapToBooking(NewBookingDto newBookingDto, User user, Item item) {
        Booking booking = new Booking();
        booking.setStart(newBookingDto.getStart());
        booking.setEnd(newBookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Booking.Status.WAITING);
        return booking;
    }

    public static BookingDtoItem mapToBookingDtoItem(Booking booking) {
        BookingDtoItem bookingDtoItem = new BookingDtoItem();
        bookingDtoItem.setId(booking.getId());
        bookingDtoItem.setBookerId(booking.getBooker().getId());
        bookingDtoItem.setStart(booking.getStart());
        bookingDtoItem.setEnd(booking.getEnd());
        return bookingDtoItem;
    }

}
