package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.NoSuchUserException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {

    @Autowired
    private final UserServiceImpl userService;

    @Autowired
    private final UserRepository userRepository;

    private User user1;

    @BeforeEach
    void fillDataBase() {
        user1 = userRepository.save(User.builder().name("user1").email("user1@yandex.ru").build());
    }

    @AfterEach
    void clearDataBase() {
        userRepository.deleteAll();
    }

    @Test
    void addUser_whenUserValid_thenUserSaved() {
        UserDto newUserDto = UserDto.builder().name("user").email("user@yandex.ru").build();
        UserDto expectedUserDto = UserDto.builder().name("user").email("user@yandex.ru").build();

        UserDto actualUserDto = userService.addUser(newUserDto);
        expectedUserDto.setId(actualUserDto.getId());

        assertThat(actualUserDto, equalTo(expectedUserDto));
    }

//    @Test
//    void addUser_whenNameNull_thenExceptionThrown() {
//        UserDto newUserDto = UserDto.builder().email("user@yandex.ru").build();
//
//        assertThrows(ConstraintViolationException.class, () -> userService.addUser(newUserDto));
//    }

//    @Test
//    void addUser_whenEmailNull_thenExceptionThrown() {
//        UserDto newUserDto = UserDto.builder().name("user").build();
//
//        assertThrows(ConstraintViolationException.class, () -> userService.addUser(newUserDto));
//    }

//    @Test
//    void addUser_whenEmailInvalid_thenExceptionThrown() {
//        UserDto newUserDto = UserDto.builder().name("user").email("user-yandex.ru").build();
//
//        assertThrows(ConstraintViolationException.class, () -> userService.addUser(newUserDto));
//    }

    @Test
    void updateUserData_whenUserValid_thenUserUpdated() {
        UserDto toUpdateUserDto = UserDto.builder().name("user1-updated").build();
        UserDto expectedUserDto = UserDto.builder()
                .id(user1.getId())
                .name("user1-updated")
                .email(user1.getEmail())
                .build();

        UserDto actualUserDto = userService.updateUserData(toUpdateUserDto, user1.getId());

        assertThat(actualUserDto, equalTo(expectedUserDto));
    }

    @Test
    void getAllUsers_whenInvoked_thenListOfDtoReturned() {
        UserDto expectedUserDto = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();

        List<UserDto> actualList = userService.getAllUsers();

        assertThat(actualList, equalTo(List.of(expectedUserDto)));
    }

    @Test
    void getUserById_whenInputValid_thenDtoReturned() {
        UserDto expectedUserDto = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();

        UserDto actualUserDto = userService.getUserById(user1.getId());

        assertThat(actualUserDto, equalTo(expectedUserDto));
    }

    @Test
    void deleteUserById_whenInvoked_thenUserDeleted() {
        userService.deleteUserById(user1.getId());

        assertThrows(NoSuchUserException.class, () -> userService.getUserById(user1.getId()));
    }

}
