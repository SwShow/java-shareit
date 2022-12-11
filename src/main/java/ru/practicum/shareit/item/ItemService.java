package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Optional<Long> userId) throws ValidationException;

    ItemDto updateItem(Optional<Long> userId, Long itemId, ItemDto itemDto) throws ValidationException;

    ItemDto getItemOfId(Long userId, Long itemId) throws ValidationException;

    List<ItemDto> getItems(Optional<Long> userId) throws ValidationException;

    List<ItemDto> getItemOfText(Optional<Long> userId, String text) throws ValidationException;
}
