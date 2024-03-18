package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class NewBookingDtoTest {

    @Autowired
    private JacksonTester<NewBookingDto> json;

    @Test
    @DisplayName("Test: NewBookingDto serialization.")
    void testNewBookingDto() throws Exception {
        NewBookingDto newBookingDto = NewBookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        JsonContent<NewBookingDto> result = json.write(newBookingDto);

        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(newBookingDto.getItemId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(newBookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(newBookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
