package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.HasNotSavedException;
import ru.practicum.shareit.exception.NoSuchEntityException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        try {
            User user = userRepository.save(UserMapper.mapToUser(userDto));
            return UserMapper.mapToUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new HasNotSavedException("User hasn't been created: " + userDto);
        }
    }

    @Override
    @Transactional
    public UserDto updateUserData(UserDto userDto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NoSuchEntityException("No user found with id = " + userId));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        try {
            return UserMapper.mapToUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new HasNotSavedException("User with id = " + userId + " hasn't been updated: " + userDto);
        }
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.mapToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.mapToUserDto(userRepository.findById(userId).orElseThrow(() ->
                new NoSuchEntityException("User with id=" + userId + " doesn't exist.")));
    }

    @Override
    @Transactional
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

}
