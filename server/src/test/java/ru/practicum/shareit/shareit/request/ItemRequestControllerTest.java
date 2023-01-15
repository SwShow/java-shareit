package ru.practicum.shareit.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.shareit.item.model.Item;
import ru.practicum.shareit.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.shareit.request.model.ItemRequest;
import ru.practicum.shareit.shareit.user.UserController;
import ru.practicum.shareit.shareit.user.UserRepository;
import ru.practicum.shareit.shareit.user.dto.UserDto;
import ru.practicum.shareit.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestControllerTest {
    @Autowired
    private ItemRequestController requestController;
    @Autowired
    private UserController userController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    private ItemRequestDto itemRequestDto;
    private UserDto userDto;
    private final User us = User.builder()
            .id(1L)
            .name("name")
            .email("user@email.com")
            .build();
    private final ItemRequest iR = ItemRequest.builder()
            .id(1L)
            .description("item request description")
            .requester(us)
            .created(LocalDateTime.now())
            .build();

    @BeforeEach
    public void clearContext() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("user@email.com");
        itemRequestDto = new ItemRequestDto(
                0L, "item request description", null, new ArrayList<>());

    }

    @Test
    void findWithItemTest() {
        UserDto user = userController.create(userDto);
        UserDto user2 = userController.create(new UserDto(0L, "name", "user2@email.com"));
        ItemRequestDto itemRequest = requestController.addItemRequest(user.getId(), itemRequestDto);
        Item item = new Item(0L, "item", "desc", true, us,
                iR);

        assertEquals(0, requestController.getItemRequestsOtherSorted(user.getId(), 0, 20).size());
        assertEquals(0, requestController.getItemRequestsOwnerSorted(user2.getId(), 0,  20).size());
    }

    @Test
    void findWithBadPagination() {
        UserDto user = userController.create(userDto);
        UserDto user2 = userController.create(new UserDto(0L, "name", "user2@email.com"));
        ItemRequestDto itemRequest = requestController.addItemRequest(user.getId(), itemRequestDto);
        Item item = new Item(0L, "item", "desc", true, us,
                iR);

        assertThrows(NoSuchElementException.class, () -> requestController.getItemRequestsOwnerSorted(
                -1L, 0, (int) user.getId()).size());
    }

    @Test
    void getAllByOwnerTest() {
        UserDto user = userController.create(userDto);
        requestController.addItemRequest(user.getId(), itemRequestDto);
        assertEquals(1, requestController.getItemRequestsOwnerSorted(user.getId(),
                0, 20).size());
    }

    @Test
    void getAllByUserWithWrongUserTest() {
        assertThrows(NoSuchElementException.class, () -> requestController.getItemRequestsOtherSorted(1L,
                0, 20));
    }

    @Test
    void getAll() {
        UserDto user = userController.create(userDto);
        requestController.addItemRequest(user.getId(), itemRequestDto);
        assertEquals(0, requestController.getItemRequestsOtherSorted(user.getId(),
                0, 10).size());

        UserDto user2 = userController.create(new UserDto(0L, "name", "user3@email.com"));
        assertEquals(1, requestController.getItemRequestsOtherSorted(
                user2.getId(), 0, 1).size());
    }

    @Test
    void getAllByWrongUser() {
        assertThrows(NoSuchElementException.class, () -> requestController.getItemRequestsOtherSorted(
                1L, 0, 10));
    }

    @Test
    void getItemRequestOfId() {
        UserDto user = userController.create(userDto);
        requestController.addItemRequest(user.getId(), itemRequestDto);
        ItemRequestDto res = requestController.getItemRequestOfId(1L, 1L);
        assertEquals("item request description", res.getDescription());
    }

}