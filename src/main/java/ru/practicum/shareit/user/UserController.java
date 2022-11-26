package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> create(@Valid @RequestBody UserDto userDto) throws ValidationException, ConflictException {
        log.info("поступил запрос на создание пользователя");
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") @Min(1) Long id, @RequestBody UserDto userDto)
            throws ConflictException {
        log.info("поступил запрос на изменение данных пользователя");
        return new ResponseEntity<>(userService.updateUser(userDto, id), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<?> findAllUsers() {
        log.info("поступил запрос на получение данных всех пользователей");
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findUserById(@PathVariable("id") @Min(1) Long id) {
        log.info("поступил запрос на получение данных пользователя");
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") @Min(1) Long id) {
        log.info("поступил запрос на получение данных пользователя");
        return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.OK);
    }
}
