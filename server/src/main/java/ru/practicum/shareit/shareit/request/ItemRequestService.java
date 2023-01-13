package ru.practicum.shareit.shareit.request;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.shareit.request.dto.ItemRequestDto;

import java.util.List;


public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getItemRequestsOwnerSorted(Long userId, PageRequest pageReq);

    List<ItemRequestDto> getItemRequestsOtherSorted(Long userId, PageRequest pageReq);

    ItemRequestDto getItemRequestOfId(Long userId, Long requestId);
}
