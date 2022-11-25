package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
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
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                              @RequestBody ItemDto itemDto) throws ValidationException {
        if (idUser.isPresent() && idUser.get() > 0) {
            log.info("поступил запрос на добавление вещи:" + itemDto);
            return itemService.createItem(itemDto, idUser.get());
        }
        throw new ValidationException("идентификатор пользователя отрицательный или отсутствует");
    }

    // только для владельца
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser, @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) throws ValidationException {
        if (idUser.isPresent() && idUser.get() > 0) {
            log.info("поступил запрос на редактирование вещи:" + itemDto + " владельцем:" + idUser);
            return itemService.updateItem(idUser.get(), itemId, itemDto);
        }
        throw new ValidationException("идентификатор пользователя отрицательный или отсутствует");
    }

    // для любого пользователя
    @GetMapping("/{itemId}")
    public ItemDto getItemOfId(@RequestHeader("X-Sharer-User-Id") Long idUser, @PathVariable Long itemId) throws ValidationException {
        if (idUser > 0 && itemId > 0) {
            log.info("поступил запрос на просмотр вещи по идентификатору:" + itemId);
            return itemService.getItemOfId(itemId);
        }
        throw new ValidationException("идентификатор пользователя отрицательный или отсутствует");
    }

    // только для владельца
    @GetMapping()
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser) throws ValidationException {
        if (idUser.isPresent() && idUser.get() > 0) {
            log.info("поступил запрос на просмотр владельцем всех своих вещей,idUser=" + idUser);
            return itemService.getItems(idUser.get());
        }
        throw new ValidationException("идентификатор пользователя отрицательный или отсутствует");
    }

    // только доступные для аренды вещи
    @GetMapping("/search")
    public List<ItemDto> getItemOfText(@RequestHeader("X-Sharer-User-Id") Optional<Long> idUser,
                                       @RequestParam("text") String text) throws ValidationException {
        if (idUser.isPresent() && idUser.get() > 0) {
            log.info("поступил запрос на просмотр доступной для аренды вещи:" + text);
            return itemService.getItemOfText(text);
        }
        throw new ValidationException("идентификатор пользователя отрицательный или отсутствует");
    }
}
