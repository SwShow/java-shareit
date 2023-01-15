package ru.practicum.shareit.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.shareit.user.dto.UserDto;
import ru.practicum.shareit.shareit.user.dto.UserMapper;
import ru.practicum.shareit.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("userDto:" + userDto);
        User user = UserMapper.INSTANCE.toUser(userDto);
        log.info("user:" + user);
        return UserMapper.INSTANCE.toDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("пользователь c идентификатором " + userId + " не существует"));
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name != null) {
            user.setName(name);
        }
        if (email != null) {
            user.setEmail(email);
        }
        user.setId(userId);
        return UserMapper.INSTANCE.toDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> findAllUsers() {
        Collection<User> list = userRepository.findAll();
        List<UserDto> listDto = new ArrayList<>();
        for (User user : list) {
            listDto.add(UserMapper.INSTANCE.toDto(user));
        }
        return listDto;
    }

    @Override
    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("пользователь c идентификатором " + id + " не существует"));
        return UserMapper.INSTANCE.toDto(user);
    }

}
