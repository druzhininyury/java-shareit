package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.HasNotSavedException;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService mockUserService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDtoAddIn;
    private UserDto userDtoAddOut;
    private UserDto userDtoUpdateIn;
    private UserDto userDtoUpdateOut;

    @BeforeEach
    void setUp() {
        userDtoAddIn = UserDto.builder()
                .name("user")
                .email("user@yandex.ru")
                .build();
        userDtoAddOut = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@yandex.ru")
                .build();
        userDtoUpdateIn = UserDto.builder()
                .name("update")
                .build();
        userDtoUpdateOut = UserDto.builder()
                .id(1L)
                .name("update")
                .email("user@yandex.ru")
                .build();
    }

    @Test
    void addUserTest() throws Exception {
        when(mockUserService.addUser(userDtoAddIn))
                .thenReturn(userDtoAddOut);
        mvc.perform(post("/users")
                    .content(mapper.writeValueAsString(userDtoAddIn))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoAddOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoAddOut.getName())))
                .andExpect(jsonPath("$.email", is(userDtoAddOut.getEmail())));
    }

    @Test
    void addUser_whenUserHasNotSavedExceptionTest() throws Exception {
        when(mockUserService.addUser(any(UserDto.class))).thenThrow(new HasNotSavedException("Error"));
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoAddIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void updateUserData() throws Exception {
        when(mockUserService.updateUserData(userDtoUpdateIn, 1L))
                .thenReturn(userDtoUpdateOut);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpdateIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdateOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdateOut.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdateOut.getEmail())));
    }

    @Test
    void updateUserData_whenNoSuchUserExceptionTest() throws Exception {
        when(mockUserService.updateUserData(any(UserDto.class), anyLong()))
                .thenThrow(new NoSuchEntityException("Error"));
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpdateIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUserData_whenUserHasNotSavedExceptionTest() throws Exception {
        when(mockUserService.updateUserData(any(UserDto.class), anyLong()))
                .thenThrow(new HasNotSavedException("Error"));
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpdateIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(mockUserService.getAllUsers())
                .thenReturn(List.of(userDtoUpdateOut));
        mvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(userDtoUpdateOut.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDtoUpdateOut.getName())))
                .andExpect(jsonPath("$[0].email", is(userDtoUpdateOut.getEmail())));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(mockUserService.getUserById(1L))
                .thenReturn(userDtoUpdateOut);
        mvc.perform(get("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdateOut.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdateOut.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdateOut.getEmail())));
    }

    @Test
    void getUserByIdTest_whenNoSuchUserExceptionTest() throws Exception {
        when(mockUserService.getUserById(anyLong()))
                .thenThrow(new NoSuchEntityException("Error"));
        mvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpdateIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        mvc.perform(delete("/users/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(mockUserService).deleteUserById(1L);
    }

}
