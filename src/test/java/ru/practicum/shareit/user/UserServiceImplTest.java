package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.NoSuchUserException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void addUser_whenUserValid_thenUserSaved() {
        long userId = 1L;
        UserDto newUserDto = UserDto.builder().name("user").email("user@yandex.ru").build();
        User user = User.builder().id(userId).name("user").email("user@yandex.ru").build();
        UserDto expectedUserDto = UserDto.builder().id(userId).name("user").email("user@yandex.ru").build();

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto actualUserDto = userService.addUser(newUserDto);

        assertThat(actualUserDto, equalTo(expectedUserDto));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserData_whenUserValid_thenUserUpdated() {
        long userId = 1L;
        UserDto toUpdateUserDto = UserDto.builder().name("user-updated").build();
        User user = User.builder().id(userId).name("user").email("user@yandex.ru").build();
        User updatedUser = User.builder().id(userId).name("user-updated").email("user@yandex.ru").build();
        UserDto expectedUserDto = UserDto.builder().id(userId).name("user-updated").email("user@yandex.ru").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto actualUserDto = userService.updateUserData(toUpdateUserDto, userId);

        assertThat(actualUserDto, equalTo(expectedUserDto));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserData_whenUserInvalid_thenExceptionThrown() {
        long userId = 2;
        UserDto toUpdateUserDto = UserDto.builder().name("user-updated").build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchUserException.class, () -> userService.updateUserData(toUpdateUserDto, userId));
    }

    @Test
    void getAllUsers_whenInputValid_thenListOfDtoReturned() {
        long userId = 1;
        User user = User.builder().id(userId).name("user").email("user@yandex.ru").build();
        UserDto expectedUserDto = UserDto.builder().id(userId).name("user").email("user@yandex.ru").build();

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> actualUserDto = userService.getAllUsers();

        assertThat(actualUserDto, equalTo(List.of(expectedUserDto)));
    }

    @Test
    void getUserById_whenInputValid_thenDtoReturned() {
        long userId = 1;
        User user = User.builder().id(userId).name("user").email("user@yandex.ru").build();
        UserDto expectedUserDto = UserDto.builder().id(userId).name("user").email("user@yandex.ru").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto actualUserDto = userService.getUserById(userId);

        assertThat(actualUserDto, equalTo(expectedUserDto));
    }

    @Test
    void getUserById_whenUserInvalid_thenExceptionThrown() {
        long userId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchUserException.class, () -> userService.getUserById(userId));
    }

    @Test
    void deleteUserById_whenInvoked_thenUserDeleted() {
        long userId = 1;

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

}
