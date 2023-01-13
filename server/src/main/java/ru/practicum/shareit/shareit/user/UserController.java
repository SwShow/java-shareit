package ru.practicum.shareit.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shareit.user.dto.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("поступил запрос на создание пользователя" + userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") Long id,
                              @RequestBody UserDto userDto) {
        log.info("поступил запрос на изменение данных пользователя");
        return userService.updateUser(userDto, id);
    }

    @GetMapping()
    public List<UserDto> findAllUsers() {
        log.info("поступил запрос на получение данных всех пользователей");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable("id") Long id) {
        log.info("поступил запрос на получение данных пользователя");
        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        log.info("поступил запрос на получение данных пользователя");
        userService.deleteUser(id);
    }
}
