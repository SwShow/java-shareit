package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper mapper;

    @Override
    public UserDto createUser(UserDto userDto) throws ConflictException {
        User user = mapper.toUser(userDto);
        return mapper.toUserDto(userStorage.createUser(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) throws ConflictException {
        User user = mapper.toUser(userDto);
        user.setIdUser(id);
        return mapper.toUserDto(userStorage.updateUser(user, id));
    }

    @Override
    public List<UserDto> findAllUsers() {
        Collection<User> list = userStorage.getAllUsers();
        List<UserDto> listDto = new ArrayList<>();
        for (User user : list) {
            listDto.add(mapper.toUserDto(user));
        }
        return listDto;
    }

    @Override
    public UserDto findUserById(Long id) {
        return mapper.toUserDto(userStorage.getUser(id));
    }

    @Override
    public UserDto deleteUser(Long id) {
        return mapper.toUserDto(userStorage.deleteUser(id));
    }
}
