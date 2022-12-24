package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;

    public ItemRequestDto(long id, String description) {

    }
}
