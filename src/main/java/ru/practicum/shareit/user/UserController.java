package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

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
    public UserDto create(@Valid @RequestBody UserDto userDto) throws ValidationException, ConflictException {
        log.info("поступил запрос на создание пользователя");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") @Min(1) Long id, @RequestBody UserDto userDto)
            throws ConflictException {
            log.info("поступил запрос на изменение данных пользователя");
            return userService.updateUser(userDto, id);
    }

    @GetMapping()
    public List<UserDto> findAllUsers() {
        log.info("поступил запрос на получение данных всех пользователей");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable("id") @Min(1) Long id) {
            log.info("поступил запрос на получение данных пользователя");
            return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable("id") @Min(1) Long id) {
            log.info("поступил запрос на получение данных пользователя");
            return userService.deleteUser(id);
    }


}
