package ru.practicum.shareit.shareit.request.dto;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.shareit.request.model.ItemRequest;

@Mapper
public interface ItemRequestMapper {

    @Mapping(target = "items", ignore = true)
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "requester", ignore = true)
    ItemRequest toItemRequest(ItemRequestDto dto);

}
