package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all items from user {}", userId);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Long id,
                                          @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("Get item {}", id);
        return itemClient.getItem(id, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Create item");
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @PathVariable("id") @Min(1) Long id,
                                             @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId) {
        log.info("Update item {}", id);
        return itemClient.updateItem(itemDto, id, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable("id") @Min(1) Long id) {
        log.info("Delete item {}", id);
        return itemClient.deleteItem(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(required = false) String text,
                                             @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @RequestParam(required = false, defaultValue = "20") Integer size) {
        log.info("Search items by text {}", text);
        return itemClient.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable("itemId") @Min(1) Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Create comment to item {}", itemId);
        return itemClient.createComment(itemId, userId, commentDto);
    }
}
