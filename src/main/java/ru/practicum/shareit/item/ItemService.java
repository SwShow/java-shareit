package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long idUser) throws ValidationException;

    ItemDto updateItem(Long idUser, Long itemId, ItemDto itemDto);

    ItemDto getItemOfId(Long itemId);

    List<ItemDto> getItems(Long idUser);

    List<ItemDto> getItemOfText(String text);
}
