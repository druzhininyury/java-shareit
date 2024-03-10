package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    @Autowired
    private final UserServiceImpl userService;

    @MockBean
    private final UserRepository userRepository;

    @Test
    void addUserTest() {
        UserDto userDtoIn = UserDto.builder()
                .name("user")
                .email("user@yandex.ru")
                .build();
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@yandex.ru")
                .build();
        when(userRepository.save(any()))
                .thenReturn(user);
        UserDto userDtoOut = userService.addUser(userDtoIn);
        assertThat(userDtoOut.getId(), equalTo(1L));
        assertThat(userDtoOut.getName(), equalTo("user"));
        assertThat(userDtoOut.getEmail(), equalTo("user@yandex.ru"));
    }

    @Test
    void updateUserDataTest() {
        UserDto userDtoIn = UserDto.builder()
                .name("updated")
                .build();
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@yandex.ru")
                .build();
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        user.setName("updated");
        when(userRepository.save(any()))
                .thenReturn(user);
        UserDto userDtoOut = userService.updateUserData(userDtoIn, 1L);
        assertThat(userDtoOut.getId(), equalTo(1L));
        assertThat(userDtoOut.getName(), equalTo("updated"));
        assertThat(userDtoOut.getEmail(), equalTo("user@yandex.ru"));
    }

    @Test
    void getAllUsersTest() {
        User user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@yandex.ru")
                .build();
        User user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@yandex.ru")
                .build();
        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));
        List<UserDto> userDtosOut = userService.getAllUsers();
        assertThat(userDtosOut, hasSize(2));
        assertThat(userDtosOut.get(0).getId(), equalTo(1L));
        assertThat(userDtosOut.get(0).getName(), equalTo("user1"));
        assertThat(userDtosOut.get(0).getEmail(), equalTo("user1@yandex.ru"));
        assertThat(userDtosOut.get(1).getId(), equalTo(2L));
        assertThat(userDtosOut.get(1).getName(), equalTo("user2"));
        assertThat(userDtosOut.get(1).getEmail(), equalTo("user2@yandex.ru"));

    }

    @Test
    void getUserByIdTest() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@yandex.ru")
                .build();
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        UserDto userDtoOut = userService.getUserById(1L);
        assertThat(userDtoOut.getId(), equalTo(1L));
        assertThat(userDtoOut.getName(), equalTo("user"));
        assertThat(userDtoOut.getEmail(), equalTo("user@yandex.ru"));

    }

    @Test
    void deleteUserByIdTest() {
        userService.deleteUserById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

}
