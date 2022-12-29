package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
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
    private ItemRequestMapper mapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    private ItemRequestDto itemRequestDto;
    private UserDto userDto;

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
    void createTest() {
        UserDto user = userController.create(userDto).getBody();
        ItemRequestDto itemRequest = requestController.addItemRequest(user.getId(), itemRequestDto).getBody();
        assert itemRequest != null;
        List<ItemDto> items = itemRequest.getItems();
        ItemRequest request = mapper.toItemRequest(itemRequest);
        User user1 = request.getRequester();

        assertEquals(1L, requestController.getItemRequestOfId(itemRequest.getId(), user.getId()).getBody().getId());
    }

    @Test
    void findWithItemTest() {
        UserDto user = userController.create(userDto).getBody();
        UserDto user2 = userController.create(new UserDto(0L, "name", "user2@email.com")).getBody();
        ItemRequestDto itemRequest = requestController.addItemRequest(user.getId(), itemRequestDto).getBody();
        Item item = new Item(0L, "item", "desc", true, userMapper.toUser(user2),
                mapper.toItemRequest(itemRequest));

        assert user2 != null;
        assertEquals(0, requestController.getItemRequestsOtherSorted(1L, 20,
                (int) user2.getId()).getBody().size());
        assertEquals(0, requestController.getItemRequestsOwnerSorted(1L, 20,
                (int) user.getId()).getBody().size());
    }

    @Test
    void findWithBadPagination() {
        UserDto user = userController.create(userDto).getBody();
        UserDto user2 = userController.create(new UserDto(0L, "name", "user2@email.com")).getBody();
        ItemRequestDto itemRequest = requestController.addItemRequest(user.getId(), itemRequestDto).getBody();
        Item item = new Item(0L, "item", "desc", true, userMapper.toUser(user2),
                mapper.toItemRequest(itemRequest));

        assertThrows(NoSuchElementException.class, () -> requestController.getItemRequestsOwnerSorted(
                -1L, 0, (int) user.getId()).getBody().size());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(NoSuchElementException.class, () -> requestController.addItemRequest(1L, itemRequestDto));
    }

    @Test
    void getAllByOwnerTest() {
        UserDto user = userController.create(userDto).getBody();
        requestController.addItemRequest(user.getId(), itemRequestDto).getBody();
        assertEquals(1, requestController.getItemRequestsOwnerSorted(user.getId(),
                0, 20).getBody().size());
    }

    @Test
    void getAllByUserWithWrongUserTest() {
        assertThrows(NoSuchElementException.class, () -> requestController.getItemRequestsOtherSorted(1L,
                0, 20));
    }

    @Test
    void getAll() {
        UserDto user = userController.create(userDto).getBody();
        requestController.addItemRequest(user.getId(), itemRequestDto);
        assertEquals(0, requestController.getItemRequestsOtherSorted(user.getId(),
                0, 10).getBody().size());

        UserDto user2 = userController.create(new UserDto(0L, "name", "user3@email.com")).getBody();
        assertEquals(1, requestController.getItemRequestsOtherSorted(
                user2.getId(), 0, 1).getBody().size());
    }

    @Test
    void getAllByWrongUser() {
        assertThrows(NoSuchElementException.class, () -> requestController.getItemRequestsOtherSorted(
                1L, 0, 10));
    }

    @Test
    void getAllWithWrongFrom() {
        userController.create(userDto);
        assertThrows(BadRequestException.class, () -> requestController.getItemRequestsOtherSorted(
                1L, -1, 10));
    }
}