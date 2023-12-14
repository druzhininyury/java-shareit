package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.NoSuchUserException;
import ru.practicum.shareit.user.dao.UserDao;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated
public class UserService {

    private UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User addUser(@Valid User user) {
        if (userDao.isEmailExists(user.getEmail())) {
            throw new EmailAlreadyExistException("Can't add user."
                    + " User with email " + user.getEmail() + " already exists.");
        }
        return userDao.addUser(user);
    }

    public User updateUserData(User user) {
        if (!userDao.isUserExists(user.getId())) {
            throw new NoSuchUserException("Can't update user." + " User with id=" + user.getId() + " doesn't exist.");
        }
        if (userDao.isEmailExists(user.getEmail()) && user.getId() != userDao.getUserByEmail(user.getEmail()).getId()) {
            throw new EmailAlreadyExistException("Can't update user."
                    + " User with email " + user.getEmail() + " already exists.");
        }
        return userDao.updateUserData(user);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public User getUserById(long userId) {
        if (!userDao.isUserExists(userId)) {
            throw new NoSuchUserException("Can't get user." + " User with id=" + userId + " doesn't exist.");
        }
        return userDao.getUserById(userId);
    }

    public void deleteUserById(long userId) {
        if (!userDao.isUserExists(userId)) {
            throw new NoSuchUserException("Can't delete user." + " User with id=" + userId + " doesn't exist.");
        }
        userDao.deleteUserById(userId);
    }

}
