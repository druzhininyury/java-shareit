package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto updateUserData(UserDto userDto, long userId);

    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    void deleteUserById(long userId);
}
