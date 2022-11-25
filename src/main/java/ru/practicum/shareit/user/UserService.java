package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto) throws ValidationException, ConflictException;
    UserDto updateUser(UserDto userDto, Long id) throws ConflictException;

    List<UserDto> findAllUsers();

    UserDto findUserById(Long id);

    UserDto deleteUser(Long id);
}
