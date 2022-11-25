package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final ItemMapper mapper;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long idUser) throws ValidationException {
        if (userStorage.getUser(idUser) == null) {
            throw new NoSuchElementException("пользователь не существует");
        }
        if (checkName(itemDto.getName()) && checkDescription(itemDto.getDescription())
                && itemDto.getAvailable() != null) {
            Item item = mapper.toItem(itemDto);
            item.setIdUser(idUser);
            return mapper.toItemDto(itemStorage.createItem(item));
        }
        throw new ValidationException("У вещи неправильно заданы параметры:" + itemDto);
    }

    public Boolean checkName(String name) throws ValidationException {
        if (name != null && !name.equals("")) {
            return true;
        }
        throw new ValidationException("имя не должно быть пустым");
    }

    public Boolean checkDescription(String desc) throws ValidationException {
        if (desc != null) {
            return true;
        }
        throw new ValidationException("описание не должно быть пустым");
    }

    @Override
    public ItemDto updateItem(Long idUser, Long itemId, ItemDto itemDto) {
        Item item = mapper.toItem(itemDto);
        log.info("вещь для редактирования:" + item);
        if (itemStorage.getItemOfId(itemId).getIdUser().equals(idUser)) {
            return mapper.toItemDto(itemStorage.updateItem(itemId, item));
        }
        throw new NoSuchElementException("нельзя редактировать чужие вещи!");
    }

    @Override
    public List<ItemDto> getItems(Long idUser) {
        List<Item> its = itemStorage.getItems(idUser);
        List<ItemDto> list = new ArrayList<>();
        for (Item item : its) {
            list.add(mapper.toItemDto(item));
        }
        return list;
    }

    @Override
    public ItemDto getItemOfId(Long itemId) {
        return mapper.toItemDto(itemStorage.getItemOfId(itemId));
    }

    @Override
    public List<ItemDto> getItemOfText(String text) {
        if (text == null || text.length() == 0) return new ArrayList<>();
        List<Item> its = itemStorage.getItemOfText(text);
        List<ItemDto> list = new ArrayList<>();
        for (Item item : its) {
            list.add(mapper.toItemDto(item));
        }
        return list;
    }
}
