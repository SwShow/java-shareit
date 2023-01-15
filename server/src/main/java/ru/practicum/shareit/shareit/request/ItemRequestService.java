package ru.practicum.shareit.shareit.request;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.shareit.request.dto.ItemRequestDto;

import java.util.List;


public interface ItemRequestService {
    ItemRequestDto addItemRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequestsOwnerSorted(long userId, PageRequest pageReq);

    List<ItemRequestDto> getItemRequestsOtherSorted(long userId, PageRequest pageReq);

    ItemRequestDto getItemRequestOfId(long userId, long requestId);
}
