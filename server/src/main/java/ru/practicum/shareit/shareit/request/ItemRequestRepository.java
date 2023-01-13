package ru.practicum.shareit.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(PageRequest pageReq, long userId);

    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(PageRequest pageReq, long userId);
}
