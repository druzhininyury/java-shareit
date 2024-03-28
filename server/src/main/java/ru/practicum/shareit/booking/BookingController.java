package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestBody NewBookingDto newBookingDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.addBooking(newBookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(@PathVariable long bookingId,
                                             @RequestParam boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.approveOrRejectBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByCurrentUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") long from,
                                                        @RequestParam(defaultValue = "10") long size) {
        return bookingService.getAllBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @RequestParam(defaultValue = "0") long from,
                                                          @RequestParam(defaultValue = "10") long size) {
        return bookingService.getAllBookingsAllItemsByOwner(userId, state, from, size);
    }

}
