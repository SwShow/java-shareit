package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

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
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        log.info("поступил запрос на создание пользователя" + userDto);
        return ResponseEntity.ok(userService.createUser(userDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        log.info("поступил запрос на изменение данных пользователя");
        return ResponseEntity.ok(userService.updateUser(userDto, id));
    }

    @GetMapping()
    public ResponseEntity<List<UserDto>> findAllUsers() {
        log.info("поступил запрос на получение данных всех пользователей");
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findUserById(@PathVariable("id") Long id) {
        log.info("поступил запрос на получение данных пользователя");
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        log.info("поступил запрос на получение данных пользователя");
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
