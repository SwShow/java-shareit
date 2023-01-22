package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> save(@RequestBody BookingDto bookingDto,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("поступил запрос на создание бронирования {}", bookingDto);
        return ResponseEntity.ok(bookingService.save(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approve(@PathVariable Long bookingId,
                                              @RequestParam Boolean approved,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("поступил запрос на изменение статуса бронирования с id {}", bookingId);
        return ResponseEntity.ok(bookingService.approve(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@PathVariable Long bookingId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("пользователем с id {} запрошено бронирование с id {}", userId, bookingId);
        return ResponseEntity.ok(bookingService.getById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllForBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(required = false, defaultValue = "ALL") String state,
                                                            @RequestParam(required = false, defaultValue = "0") Integer from,
                                                            @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.info("поступил запрос на получение списка бронирований со статусом {} пользователя с id {}", state, userId);
        return ResponseEntity.ok(bookingService.findAllForBooker(from, size, userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllForOwner(@RequestHeader("X-Sharer-User-Id") Long owner,
                                                           @RequestParam(required = false, defaultValue = "ALL") String state,
                                                           @RequestParam(required = false, defaultValue = "0") Integer from,
                                                           @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.info("поступил запрос на получение списка бронирований со статусом {} для вещей пользователя с id {}", state, owner);
        return ResponseEntity.ok(bookingService.findAllForOwner(from, size, owner, state));
    }
}
