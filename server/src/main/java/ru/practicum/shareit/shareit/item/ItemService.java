package ru.practicum.shareit.shareit.item;

import ru.practicum.shareit.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.shareit.item.comment.dto.CommentDtoLittle;
import ru.practicum.shareit.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(long userId, Long itemId, ItemDto itemDto);

    ItemDto getItemOfId(Long userId, Long itemId);

    List<ItemDto> getItems(long userId);

    List<ItemDto> getItemOfText(long userId, String text);

    CommentDto createComment(CommentDtoLittle commentDtoLittle, long itemId, long userId);
}
