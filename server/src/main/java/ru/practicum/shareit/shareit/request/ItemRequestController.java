package ru.practicum.shareit.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shareit.request.dto.ItemRequestDto;

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
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody ItemRequestDto text) {
        log.info("поступил запрос от пользователя {} на добавление запроса вещи {}", userId, text);
        return service.addItemRequest(userId, text);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsOwnerSorted(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @RequestParam(required = false, defaultValue = "0") int from,
                                                           @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("поступил запрос пользователя {} на получение  списка своих запросов вместе с данными об ответах на них", userId);
        return service.getItemRequestsOwnerSorted(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequestsOtherSorted(@RequestHeader("X-Sharer-User-Id") long userId,
                                                           @RequestParam(required = false, defaultValue = "0") int from,
                                                           @RequestParam(required = false, defaultValue = "20") int size) {
        log.info("поступил запрос от пользователя {} на получение списка запросов, созданных другими пользователями", userId);
        return service.getItemRequestsOtherSorted(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{id}")
    public ItemRequestDto getItemRequestOfId(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("id") long requestId) {
        log.info("поступил запрос на получение пользователем {} данных об одном " +
                "конкретном запросе id {} вместе с данными об ответах на него", userId, requestId);
        return service.getItemRequestOfId(userId, requestId);
    }
}
