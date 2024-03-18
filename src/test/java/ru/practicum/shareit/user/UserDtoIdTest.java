package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDtoId;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoIdTest {

    @Autowired
    private JacksonTester<UserDtoId> json;

    @Test
    @DisplayName("Test: UserDtoId serialization.")
    void testUserDtoId() throws Exception {
        UserDtoId userDtoId = UserDtoId.builder().id(1L).build();

        JsonContent<UserDtoId> result = json.write(userDtoId);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(userDtoId.getId().intValue());

    }

}
