package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UsergDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequestMapping("/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UsergateClient usergateClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get all users");
        return usergateClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") @Min(1) Long id) {
        log.info("Get user {}", id);
        return usergateClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UsergDto userDto) {
        log.info("Creating user{}", userDto);
        return usergateClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UsergDto userDto,
                                             @PathVariable("id") @Min(1) Long id) {
        log.info("Update user {}", id);
        return usergateClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") @Min(1) Long id) {
        log.info("поступил запрос на получение данных пользователя");
        usergateClient.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
