package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item createItem(Item item) {
        item.setId(createId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Item item1) {
        Item item = items.get(itemId);
        if (item1.getAvailable() != null) item.setAvailable(item1.getAvailable());
        if (item1.getName() != null && !Objects.equals(item1.getName(), "")) item.setName(item1.getName());
        if (item1.getDescription() != null && !Objects.equals(item1.getDescription(), ""))
            item.setDescription(item1.getDescription());
        return item;
    }

    @Override
    public Item getItemOfId(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItems(Long idUser) {
        List<Item> list = new ArrayList<>();
        for (Item i : items.values()) {
            if (i.getIdUser().equals(idUser)) list.add(i);
        }
        return list;
    }

    @Override
    public List<Item> getItemOfText(String text) {
        List<Item> list = new ArrayList<>();
        for (Item i : items.values()) {
            boolean a = i.getAvailable().equals(true) && i.getDescription().toLowerCase().contains(text.toLowerCase());
            boolean b = i.getAvailable().equals(true) && i.getName().toLowerCase().contains(text.toLowerCase());
            if (a || b) list.add(i);
        }
        return list;
    }

    protected Long createId() {
        return ++id;
    }
}
