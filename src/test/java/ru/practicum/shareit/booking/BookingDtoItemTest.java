package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoItemTest {

    @Autowired
    private JacksonTester<BookingDtoItem> json;

    @Test
    void testBookingDtoItem() throws Exception {
        BookingDtoItem bookingDtoItem = BookingDtoItem.builder()
                .id(2L)
                .bookerId(3L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        JsonContent<BookingDtoItem> result = json.write(bookingDtoItem);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingDtoItem.getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingDtoItem.getBookerId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDtoItem.getStart().format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDtoItem.getEnd().format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
