package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerTest {

    @Autowired
    private ItemController itemController;
    @Autowired
    private UserController userController;

   /* @Autowired
    private ItemRequestController requestController;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemService itemService;*/

    ItemDto itemDto = new ItemDto(0L, "item_name", "item_description", true, null,
            null, new ArrayList<>(), 0L);

    UserDto userDto = new UserDto(0L, "user_name", "username@email.com");

   /* ItemRequestDto requestDto = new ItemRequestDto(0L, "item_request_description");

    CommentDto comment = new CommentDto(0L, "comment", null, null);*/

    @Test
    void createItem() {
        UserDto userDto1 = userController.create(userDto).getBody();
        System.out.println(userDto1);
        ResponseEntity<ItemDto> itemDto1 = itemController.createItem(Optional.of(1L), itemDto);
        assert itemDto1 != null;
        assertEquals(OK, itemDto1.getStatusCode());
        ResponseEntity<ItemDto> itemDto2 = itemController.getItemOfId(itemDto1.getBody().getId(), userDto1.getId());
        assertEquals(OK, itemDto2.getStatusCode());
        assertEquals(itemDto1.getBody().getId(), userDto1.getId());
    }

    @Test
    void updateItem() {
        userController.create(userDto);
        itemController.createItem(Optional.of(1L), itemDto);
        ItemDto itemDto1 = new ItemDto(0L, "new name", "updateDescription", false,
                null, null, new ArrayList<>(), 0L);
        ResponseEntity<ItemDto> itemDto2 = itemController.updateItem(Optional.of(1L), 1L, itemDto1);
        assertEquals(OK, itemDto2.getStatusCode());
        ItemDto itemDto3 = itemController.getItemOfId(1L, 1L).getBody();
        assert itemDto3 != null;
        assertEquals("new name", itemDto3.getName());
        assertEquals("updateDescription", itemDto3.getDescription());
    }

    @Test
    void getItems() {
        userController.create(userDto);
        itemController.createItem(Optional.of(1L), itemDto);
        ResponseEntity<List<ItemDto>> list = itemController.getItems(Optional.of(1L));
        assertEquals(OK, list.getStatusCode());
        assertEquals(1, Objects.requireNonNull(list.getBody()).size());
    }

    @Test
    void getItemOfText() {
        userController.create(userDto);
        itemController.createItem(Optional.of(1L), itemDto);
        ResponseEntity<List<ItemDto>> list = itemController.getItemOfText(Optional.of(1L), "Item");
        assertEquals(OK, list.getStatusCode());
        assertEquals(1, Objects.requireNonNull(list.getBody()).size());
    }

   /* @Test
    void createComment() {
    }*/
}