package ru.practicum.shareit.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.shareit.exception.ValidationException;
import ru.practicum.shareit.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.shareit.item.comment.dto.CommentDtoLittle;
import ru.practicum.shareit.shareit.item.dto.ItemDto;

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
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                              @RequestBody ItemDto itemDto) throws ValidationException {
        log.info("поступил запрос на добавление вещи:" + itemDto + " пользователем:" + userId);

        return itemService.createItem(itemDto, userId);
    }

    // только для владельца
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) throws ValidationException {
        log.info("поступил запрос на редактирование вещи:" + itemDto + " владельцем:" + userId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    // для любого пользователя
    @GetMapping("/{itemId}")
    public ItemDto getItemOfId(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) throws ValidationException {
        log.info("поступил запрос на просмотр вещи по идентификатору:" + itemId);
        return itemService.getItemOfId(userId, itemId);
    }

    // только для владельца
    @GetMapping()
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId) throws ValidationException {
        log.info("поступил запрос на просмотр владельцем всех своих вещей,idUser=" + userId);
        return itemService.getItems(userId);
    }

    // только доступные для аренды вещи
    @GetMapping("/search")
    public List<ItemDto> getItemOfText(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                       @RequestParam("text") String text) throws ValidationException {
        log.info("поступил запрос на просмотр доступной для аренды вещи:" + text);
        return itemService.getItemOfText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDtoLittle commentDtoLittle,
                                    @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("пользователь с id {} оставил отзыв на вещь с id {}: {}", userId, itemId, commentDtoLittle);

        return itemService.createComment(commentDtoLittle, itemId, userId);
    }
}
