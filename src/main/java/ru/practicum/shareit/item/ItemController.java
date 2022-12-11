package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
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
    public ResponseEntity<?> createItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                        @Valid @RequestBody ItemDto itemDto) throws ValidationException {
        log.info("поступил запрос на добавление вещи:" + itemDto + " пользователем:" + userId);
        return new ResponseEntity<>(itemService.createItem(itemDto, userId), HttpStatus.CREATED);
    }

    // только для владельца
    @PatchMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId, @PathVariable Long itemId,
                                        @RequestBody ItemDto itemDto) throws ValidationException {
        log.info("поступил запрос на редактирование вещи:" + itemDto + " владельцем:" + userId);
        return new ResponseEntity<>(itemService.updateItem(userId, itemId, itemDto), HttpStatus.OK);
    }

    // для любого пользователя
    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItemOfId(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) throws ValidationException {
        log.info("поступил запрос на просмотр вещи по идентификатору:" + itemId);
        return new ResponseEntity<>(itemService.getItemOfId(userId, itemId), HttpStatus.OK);
    }

    // только для владельца
    @GetMapping()
    public ResponseEntity<?> getItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) throws ValidationException {
        log.info("поступил запрос на просмотр владельцем всех своих вещей,idUser=" + userId);
        return new ResponseEntity<>(itemService.getItems(userId), HttpStatus.OK);
    }

    // только доступные для аренды вещи
    @GetMapping("/search")
    public ResponseEntity<?> getItemOfText(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                           @RequestParam("text") String text) throws ValidationException {
        log.info("поступил запрос на просмотр доступной для аренды вещи:" + text);
        return new ResponseEntity<>(itemService.getItemOfText(userId, text), HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentDto commentDto,
                                           @PathVariable Long itemId,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("пользователь с id {} оставил отзыв на вещь с id {}: {}", userId, itemId, commentDto);

        return new ResponseEntity<>(itemService.createComment(commentDto, itemId, userId), HttpStatus.OK);
    }
}
