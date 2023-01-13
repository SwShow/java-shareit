package ru.practicum.shareit.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shareit.booking.dto.BookingDto;

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
    public BookingDto save(@RequestBody BookingDto bookingDto,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("поступил запрос на создание бронирования {}", bookingDto);
        return bookingService.save(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable long bookingId,
                              @RequestParam Boolean approved,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("поступил запрос на изменение статуса бронирования с id {}", bookingId);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable long bookingId,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("пользователем с id {} запрошено бронирование с id {}", userId, bookingId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllForBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(required = false, defaultValue = "ALL") String state,
                                            @RequestParam(required = false, defaultValue = "0") int from,
                                            @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("поступил запрос на получение списка бронирований со статусом {} пользователя с id {}", state, userId);
        return bookingService.findAllForBooker(from, size, userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllForOwner(@RequestHeader("X-Sharer-User-Id") long owner,
                                           @RequestParam(required = false, defaultValue = "ALL") String state,
                                           @RequestParam(required = false, defaultValue = "0") int from,
                                           @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("поступил запрос на получение списка бронирований со статусом {} для вещей пользователя с id {}", state, owner);
        return bookingService.findAllForOwner(from, size, owner, state);
    }
}
