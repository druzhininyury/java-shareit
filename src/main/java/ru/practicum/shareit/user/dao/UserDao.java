package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserDao {

    User addUser(User user);
    User updateUserData(User user);
    boolean isUserExists(long userId);
    boolean isEmailExists(String email);
    User getUserById(long userId);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    void deleteUserById(long userId);

}
