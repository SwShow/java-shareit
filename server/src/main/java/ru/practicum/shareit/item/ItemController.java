package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoLittle;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                              @RequestBody ItemDto itemDto) throws ValidationException {
        log.info("поступил запрос на добавление вещи:" + itemDto + " пользователем:" + userId);

        return ResponseEntity.ok(itemService.createItem(itemDto, userId));
    }

    // только для владельца
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) throws ValidationException {
        log.info("поступил запрос на редактирование вещи:" + itemDto + " владельцем:" + userId);
        return ResponseEntity.ok(itemService.updateItem(userId, itemId, itemDto));
    }

    // для любого пользователя
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemOfId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long itemId) throws ValidationException {
        log.info("поступил запрос на просмотр вещи по идентификатору:" + itemId);
        return ResponseEntity.ok(itemService.getItemOfId(userId, itemId));
    }

    // только для владельца
    @GetMapping()
    public ResponseEntity<List<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) throws ValidationException {
        log.info("поступил запрос на просмотр владельцем всех своих вещей,idUser=" + userId);
        return ResponseEntity.ok(itemService.getItems(userId));
    }

    // только доступные для аренды вещи
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> getItemOfText(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                                       @RequestParam("text") String text) throws ValidationException {
        log.info("поступил запрос на просмотр доступной для аренды вещи:" + text);
        return ResponseEntity.ok(itemService.getItemOfText(userId, text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@Valid @RequestBody CommentDtoLittle commentDtoLittle,
                                                    @PathVariable Long itemId,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("пользователь с id {} оставил отзыв на вещь с id {}: {}", userId, itemId, commentDtoLittle);

        return ResponseEntity.ok(itemService.createComment(commentDtoLittle, itemId, userId));
    }
}
