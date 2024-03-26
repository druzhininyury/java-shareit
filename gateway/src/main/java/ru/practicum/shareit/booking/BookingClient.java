package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.client.BaseClient;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@Service
@Validated
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(@Valid NewBookingDto newBookingDto, long userId) {
        return post("/", userId, newBookingDto);
    }

    public ResponseEntity<Object> approveOrRejectBooking(long bookingId, long userId, boolean approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingById(long bookingId, long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingsByUser(
            long userId, String state, @PositiveOrZero long from, @Positive long size) {
        BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size);
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsAllItemsByOwner(
            long userId, String state, @PositiveOrZero long from, @Positive long size) {
        BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size);
        return get("/owner/?state={state}&from={from}&size={size}", userId, parameters);
    }
}
