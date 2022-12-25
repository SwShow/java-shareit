package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoLittle;
import ru.practicum.shareit.item.dto.ItemDto;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;
    @Mock
    private ItemService itemService;

    ItemDto itemDto1 = new ItemDto(0L, "item_name", "item_description", true, null,
            null, new ArrayList<>(), 0L);
    ItemDto itemDto2 = new ItemDto(1L, "item_name", "item_description", true, null,
            null, new ArrayList<>(), 0L);
    ItemDto itemDto3 = new ItemDto(1L, "update_name", "update_description", true, null,
            null, new ArrayList<>(), 0L);

    CommentDtoLittle comment = new CommentDtoLittle("comment", 0L, 0L);

    CommentDto commentDto = new CommentDto(1L, "comment", "author_name", LocalDateTime.now());

    @Test
    void createItem() {
        when(itemService.createItem(itemDto1, Optional.of(1L)))
                .thenReturn(itemDto2);
        ResponseEntity<ItemDto> res = itemController.createItem(Optional.of(1L), itemDto1);
        assert itemDto1 != null;
        assertEquals(OK, res.getStatusCode());
        assertEquals(Objects.requireNonNull(res.getBody()).getId(), itemDto2.getId());
    }

    @Test
    void updateItem() {
        when(itemService.updateItem(Optional.of(1L), 1L, itemDto3))
                .thenReturn(itemDto3);
        ResponseEntity<ItemDto> res = itemController.updateItem(Optional.of(1L), 1L, itemDto3);
        assertEquals(OK, res.getStatusCode());
        assertEquals("update_name", Objects.requireNonNull(res.getBody()).getName());
        assertEquals("update_description", res.getBody().getDescription());
    }

    @Test
    void getItems() {
        when(itemService.getItems(Optional.of(1L)))
                .thenReturn(List.of(itemDto2));

        ResponseEntity<List<ItemDto>> list = itemController.getItems(Optional.of(1L));
        assertEquals(OK, list.getStatusCode());
        assertEquals(1, Objects.requireNonNull(list.getBody()).size());
    }

    @Test
    void getItemOfText() {
        when(itemService.getItemOfText(Optional.of(1L), "uPd"))
                .thenReturn(List.of(itemDto3));

        ResponseEntity<List<ItemDto>> list = itemController.getItemOfText(Optional.of(1L), "uPd");
        assertEquals(OK, list.getStatusCode());
        assertEquals(1, Objects.requireNonNull(list.getBody()).size());
    }

    @Test
    void createComment() {
        when(itemService.createComment(comment, 1L, 1L))
                .thenReturn(commentDto);
        ResponseEntity<CommentDto> res = itemController.createComment(comment, 1L, 1L);
        assertEquals(OK, res.getStatusCode());
        assertEquals(1L, Objects.requireNonNull(res.getBody()).getId());
        assertEquals("author_name", res.getBody().getAuthorName());
    }

}
