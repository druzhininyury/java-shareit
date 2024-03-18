package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    @DisplayName("Test: ItemDto serialization.")
    void testItemDto() throws Exception {
        BookingDtoItem lastBooking = BookingDtoItem.builder()
                .id(1L)
                .bookerId(2L)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .build();
        BookingDtoItem nextBooking = BookingDtoItem.builder()
                .id(2L)
                .bookerId(3L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("user2")
                .created(LocalDateTime.now().minusDays(1))
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("description")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(comment))
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());

        assertThat(result).hasJsonPath("$.lastBooking.id");
        assertThat(result).hasJsonPath("$.lastBooking.bookerId");
        assertThat(result).hasJsonPath("$.lastBooking.start");
        assertThat(result).hasJsonPath("$.lastBooking.end");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(itemDto.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(itemDto.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(itemDto.getLastBooking().getStart().format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(itemDto.getLastBooking().getEnd().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(result).hasJsonPath("$.nextBooking.id");
        assertThat(result).hasJsonPath("$.nextBooking.bookerId");
        assertThat(result).hasJsonPath("$.nextBooking.start");
        assertThat(result).hasJsonPath("$.nextBooking.end");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(itemDto.getNextBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(itemDto.getNextBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(itemDto.getNextBooking().getStart().format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(itemDto.getNextBooking().getEnd().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(result).hasJsonPath("$.comments[0]");
        assertThat(result).hasJsonPath("$.comments[0].id");
        assertThat(result).hasJsonPath("$.comments[0].text");
        assertThat(result).hasJsonPath("$.comments[0].authorName");
        assertThat(result).hasJsonPath("$.comments[0].created");
        assertThat(result).doesNotHaveJsonPath("$.comments[1]");
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(itemDto.getComments().get(0).getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo(itemDto.getComments().get(0).getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo(itemDto.getComments().get(0).getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(itemDto.getComments().get(0).getCreated().format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
