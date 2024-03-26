package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.NewBookingDto;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestBody NewBookingDto newBookingDto,
								 @RequestHeader("X-Sharer-User-Id") long userId) {
		return bookingClient.addBooking(newBookingDto, userId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveOrRejectBooking(@PathVariable long bookingId,
											 @RequestParam boolean approved,
											 @RequestHeader("X-Sharer-User-Id") long userId) {
		return bookingClient.approveOrRejectBooking(bookingId, userId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@PathVariable long bookingId,
									 @RequestHeader("X-Sharer-User-Id") long userId) {
		return bookingClient.getBookingById(bookingId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBookingsByCurrentUser(@RequestHeader("X-Sharer-User-Id") long userId,
														@RequestParam(defaultValue = "ALL") String state,
														@RequestParam(defaultValue = "0") long from,
														@RequestParam(defaultValue = "10") long size) {
		return bookingClient.getAllBookingsByUser(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
														  @RequestParam(defaultValue = "ALL") String state,
														  @RequestParam(defaultValue = "0") long from,
														  @RequestParam(defaultValue = "10") long size) {
		return bookingClient.getAllBookingsAllItemsByOwner(userId, state, from, size);
	}

}
