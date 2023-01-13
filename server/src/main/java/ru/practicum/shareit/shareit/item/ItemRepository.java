package ru.practicum.shareit.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" select i from Item i " +
            "where i.available = true and" +
            " (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "  or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String text);

    List<Item> findAllByOwnerId(long ownerId);

    List<Item> findItemsByRequestId(Long requestId);

    @Query(" select i from Item i " +
            "where i.request is not null")
    List<Item> findAllWithNonNullRequest();
}