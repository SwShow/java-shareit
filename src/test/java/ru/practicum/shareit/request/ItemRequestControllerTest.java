package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestControllerTest {

    @Autowired
    private ItemRequestController itemRequestController;
    @Autowired
    private UserController userController;
    ItemRequestDto itemRequestDto = new ItemRequestDto(0L, "description", null, new ArrayList<>());
    UserDto userDto = new UserDto(0L, "user_name", "username@yandex.ru");

    @Test
    void addItemRequest() {
        UserDto userDto1 = userController.create(userDto).getBody();
        assert userDto1 != null;
        ResponseEntity<ItemRequestDto> dto1 = itemRequestController.addItemRequest(userDto1.getId(), itemRequestDto);
        ResponseEntity<ItemRequestDto> dto2 = itemRequestController.getItemRequestOfId(userDto1.getId(), dto1.getBody().getId());
        assertEquals(OK, dto1.getStatusCode());
        assertEquals(OK, dto2.getStatusCode());
        assertEquals(1L, dto2.getBody().getId());
        assertEquals("description", dto2.getBody().getDescription());
    }

    @Test
    void getItemRequestsOwnerSorted() {
        UserDto userDto1 = userController.create(userDto).getBody();
        assert userDto1 != null;
        itemRequestController.addItemRequest(userDto1.getId(), itemRequestDto);
        assertEquals(1, itemRequestController.getItemRequestsOwnerSorted(userDto1.getId(), 0, 5).getBody().size());
    }

    @Test
    void getItemRequestsOtherSorted() {
        UserDto userDto1 = userController.create(userDto).getBody();
        assert userDto1 != null;
        itemRequestController.addItemRequest(userDto1.getId(), itemRequestDto);
        UserDto userDto2 = userController.create(new UserDto(0, "new_user_name", "new@email.com")).getBody();
        assert userDto2 != null;
        assertEquals(1, Objects.requireNonNull(itemRequestController.getItemRequestsOtherSorted(userDto2.getId(), 0, 5).getBody()).size());
    }

    @Test
    void getItemRequestOfWrongUserId() {
        assertThrows(NoSuchElementException.class, () -> itemRequestController.addItemRequest(userDto.getId(), itemRequestDto));
    }
}