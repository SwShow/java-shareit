package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody BookingDto bookingDto,
                               @RequestHeader(HEADER) long userId) {
        log.info("поступил запрос на создание бронирования {}", bookingDto);
        return new ResponseEntity<>(bookingService.save(bookingDto, userId), HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<?> approve(@PathVariable long bookingId,
                              @RequestParam Boolean approved,
                              @RequestHeader(HEADER) long userId) {
        log.info("поступил запрос на изменение статуса бронирования с id {}", bookingId);
        return new ResponseEntity<>(bookingService.approve(userId, bookingId, approved), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getById(@PathVariable long bookingId,
                              @RequestHeader(HEADER) long userId) {
        log.info("пользователем с id {} запрошено бронирование с id {}", userId, bookingId);
        return new ResponseEntity<>(bookingService.getById(bookingId, userId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllForBooker(@RequestHeader(HEADER) long userId,
                                            @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("поступил запрос на получение списка бронирований со статусом {} пользователя с id {}", state, userId);
        return new ResponseEntity<>(bookingService.findAllForBooker(userId, state), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<?> getAllForOwner(@RequestHeader(HEADER) long owner,
                                           @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("поступил запрос на получение списка бронирований со статусом {} для вещей пользователя с id {}", state, owner);
        return new ResponseEntity<>(bookingService.findAllForOwner(owner, state), HttpStatus.OK);
    }
}
