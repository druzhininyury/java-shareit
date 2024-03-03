package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.exception.NoSuchUserException;
import ru.practicum.shareit.user.exception.UserHasNotSavedException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto addUser(@Valid UserDto userDto) {
        try {
            User user = userRepository.save(UserMapper.mapToUser(userDto));
            return UserMapper.mapToUserDto(user);
        } catch(DataIntegrityViolationException e) {
            throw new UserHasNotSavedException("User hasn't been created: " + userDto);
        }
    }

    @Override
    @Transactional
    public UserDto updateUserData(UserDto userDto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("No user found with id = " + userId));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        try {
            return UserMapper.mapToUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new UserHasNotSavedException("User with id = " + userId + " hasn't been updated: " + userDto);
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.mapToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.mapToUserDto(userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("User with id=" + userId + " doesn't exist.")));
    }

    @Override
    @Transactional
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

}
