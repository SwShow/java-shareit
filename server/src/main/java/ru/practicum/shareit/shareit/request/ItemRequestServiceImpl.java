package ru.practicum.shareit.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.shareit.exception.ValidationException;
import ru.practicum.shareit.shareit.item.ItemRepository;
import ru.practicum.shareit.shareit.item.dto.ItemDto;
import ru.practicum.shareit.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.shareit.item.model.Item;
import ru.practicum.shareit.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.shareit.request.model.ItemRequest;
import ru.practicum.shareit.shareit.user.UserRepository;
import ru.practicum.shareit.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final ItemRequestMapper mapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().equals("")) {
            throw new ValidationException("отзыв не может быть пустым");
        }
        User user = getUser(userId);
        ItemRequest itemRequest = mapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now().minusHours(3));
        return mapper.toItemRequestDto(repository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getItemRequestsOwnerSorted(Long userId, PageRequest pageReq) {
        getUser(userId);
        List<ItemRequest> requests = repository.findAllByRequesterIdOrderByCreatedDesc(pageReq, userId);
        return addItemsToRequest(requests);
    }

    @Override
    public List<ItemRequestDto> getItemRequestsOtherSorted(Long userId, PageRequest pageReq) {
        getUser(userId);
        List<ItemRequest> requests = repository.findAllByRequesterIdNotOrderByCreatedDesc(pageReq, userId);
        return addItemsToRequest(requests);
    }

    @Override
    public ItemRequestDto getItemRequestOfId(Long userId, Long requestId) {
        getUser(userId);

        ItemRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("запроса с идентификатором " + requestId + " нет"));

        ItemRequestDto requestDto = mapper.toItemRequestDto(request);

        requestDto.setItems(itemRepository.findItemsByRequestId(requestId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList()));

        return requestDto;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("пользователя c идентификатором " + userId + " нет"));
    }

    private List<ItemRequestDto> addItemsToRequest(List<ItemRequest> requests) {
        List<Item> items = itemRepository.findAllWithNonNullRequest();
        List<ItemRequestDto> itemRequestDto = new ArrayList<>();

        for (ItemRequest iR : requests) {
            List<ItemDto> itemDto = new ArrayList<>();
            for (Item i : items) {
                if (iR.getId() == i.getRequest().getId()) {
                    itemDto.add(itemMapper.toItemDto(i));
                }
            }
            ItemRequestDto dto = mapper.toItemRequestDto(iR);
            dto.setItems(itemDto);
            itemRequestDto.add(dto);
        }
        return itemRequestDto;
    }

}
