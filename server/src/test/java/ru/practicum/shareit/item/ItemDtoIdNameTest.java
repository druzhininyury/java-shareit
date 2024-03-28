package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoIdName;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoIdNameTest {

    @Autowired
    private JacksonTester<ItemDtoIdName> json;

    @Test
    @DisplayName("Test: ItemDtoIdName serialization.")
    void testItemDto() throws Exception {
        ItemDtoIdName itemDtoIdName = ItemDtoIdName.builder()
                .id(1L)
                .name("item")
                .build();

        JsonContent<ItemDtoIdName> result = json.write(itemDtoIdName);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDtoIdName.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDtoIdName.getName());
    }
}
