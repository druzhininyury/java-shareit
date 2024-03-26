package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("request")
                .requester(2L)
                .created(LocalDateTime.now())
                .items(List.of(ItemDto.builder()
                        .id(1L)
                        .name("item")
                        .description("description")
                        .available(true)
                        .requestId(1L)
                        .build()))
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.requester");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requester")
                .isEqualTo(itemRequestDto.getRequester().intValue());
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME));

        assertThat(result).hasJsonPath("$.items[0]");
        assertThat(result).doesNotHaveJsonPath("$.items[1]");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(itemRequestDto.getItems().get(0).getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(itemRequestDto.getItems().get(0).getName());
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(itemRequestDto.getItems().get(0).getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(itemRequestDto.getItems().get(0).getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(itemRequestDto.getItems().get(0).getRequestId().intValue());
    }

}
