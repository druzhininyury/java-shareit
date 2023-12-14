package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NoSuchUserException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDaoInMemoryImpl implements UserDao {

    private long nextId = 1;

    private Map<Long, User> usersById = new HashMap<>();
    private Map<String, User> usersByEmail = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        usersById.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User updateUserData(User user) {
        User inMemoryUser = usersById.get(user.getId());
        if (user.getName() != null) {
            inMemoryUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            usersByEmail.remove(inMemoryUser.getEmail());
            inMemoryUser.setEmail(user.getEmail());
            usersByEmail.put(inMemoryUser.getEmail(), inMemoryUser);
        }
        return inMemoryUser;
    }

    public boolean isUserExists(long userId) {
        return usersById.containsKey(userId);
    }

    public boolean isEmailExists(String email) {
        return usersByEmail.containsKey(email);
    }

    @Override
    public User getUserById(long userId) {
        if (!isUserExists(userId)) {
            throw new NoSuchUserException("No user exists with id=" + userId);
        }
        return usersById.get(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        if (!isEmailExists(email)) {
            throw new NoSuchUserException("No user exists with email=" + email);
        }
        return usersByEmail.get(email);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList(usersById.values());
    }

    @Override
    public void deleteUserById(long userId) {
        User user = getUserById(userId);
        usersByEmail.remove(user.getEmail());
        usersById.remove(user.getId());
    }

    private long getNextId() {
        return nextId++;
    }
}
