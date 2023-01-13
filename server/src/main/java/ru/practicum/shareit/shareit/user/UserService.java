package ru.practicum.shareit.shareit.user;

import ru.practicum.shareit.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long id);

    List<UserDto> findAllUsers();

    UserDto findUserById(Long id);

    void deleteUser(Long id);
}
