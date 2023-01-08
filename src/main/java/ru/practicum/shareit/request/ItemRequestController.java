package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */


@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ResponseEntity<ItemRequestDto> addItemRequest(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                            @RequestBody ItemRequestDto text) {
        log.info("поступил запрос от пользователя {} на добавление запроса вещи {}", userId, text);
        return ResponseEntity.ok(service.addItemRequest(userId, text));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getItemRequestsOwnerSorted(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                                           @RequestParam(required = false, defaultValue = "0") int from,
                                                                           @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("поступил запрос пользователя {} на получение  списка своих запросов вместе с данными об ответах на них", userId);
        if (from < 0 || size <= 0) {
            throw new BadRequestException("параметры пагинации не могут быть отрицательными или равны нулю");
        }
        return ResponseEntity.ok(service.getItemRequestsOwnerSorted(userId, PageRequest.of(from / size, size)));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getItemRequestsOtherSorted(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                        @RequestParam(required = false, defaultValue = "0") int from,
                                                        @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("поступил запрос от пользователя {} на получение списка запросов, созданных другими пользователями", userId);
        if (from < 0 || size <= 0) {
            throw new BadRequestException("параметры пагинации не могут быть отрицательными или равны нулю");
        }
        return ResponseEntity.ok(service.getItemRequestsOtherSorted(userId, PageRequest.of(from / size, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemRequestDto> getItemRequestOfId(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                      @PathVariable("id") @Min(1) Long requestId) {
        log.info("поступил запрос на получение пользователем {} данных об одном " +
                "конкретном запросе id {} вместе с данными об ответах на него", userId, requestId);
        return ResponseEntity.ok(service.getItemRequestOfId(userId, requestId));
    }
}
