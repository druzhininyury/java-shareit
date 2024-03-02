package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

public interface UserService {

    UserDto addUser(@Valid UserDto userDto);

    UserDto updateUserData(UserDto userDto, long userId);

    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    void deleteUserById(long userId);
}
