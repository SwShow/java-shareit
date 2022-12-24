package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoLittle;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpStatus.OK;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerTest {
    @Autowired
    private ItemController itemController;
    @Autowired
    private UserController userController;
    @Autowired
    private BookingController bookingController;
    @Autowired
            private ItemMapper itemMapper;
    @Autowired
            private UserMapper userMapper;
    @Autowired
            private CommentMapper commentMapper;

    ItemDto itemDto = new ItemDto(0L, "item_name", "item_description", true, null,
            null, new ArrayList<>(), 0L);

    UserDto userDto = new UserDto(0L, "user_name", "username@email.com");

    CommentDtoLittle comment = new CommentDtoLittle("comment", 0L, 0L);

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

    @Test
    void createComment() {
        Comment newComment = new Comment(0L, "Какой-то коммент", itemMapper.toItem(itemDto),
                userMapper.toUser(userDto), LocalDateTime.now());
        CommentDto newCommentDto = commentMapper.toCommentDto(newComment);
        newCommentDto.setAuthorName(userMapper.toUser(userDto).getName());
        UserDto user = userController.create(userDto).getBody();
        ItemDto item = itemController.createItem(Optional.of(1L), itemDto).getBody();
        ItemDto.BookingForItemDto lastBooking = item.getLastBooking();
        ItemDto.BookingForItemDto nextBooking = item.getNextBooking();
        ItemDto.BookingForItemDto booking = new ItemDto.BookingForItemDto(0L,
                LocalDateTime.of(2022, 12, 30, 12, 30),
                LocalDateTime.of(2023, 11, 10, 13, 0), user.getId());
        booking.setId(1L);
        UserDto user2 = userController.create(new UserDto(0L, "name", "email2@email.com")).getBody();
        bookingController.save(new BookingDto(0L, 1L,
                LocalDateTime.of(2022, 12, 30, 12, 30),
                LocalDateTime.of(2023, 11, 10, 13, 0),
                itemDto,user2, WAITING), user2.getId());
        bookingController.approve(1L, true, 1L);
        Comment comment1 = commentMapper.toComment(comment);
        comment1.setItem(itemMapper.toItem(itemDto));
        comment1.setAuthor(userMapper.toUser(userDto));
        User user1 = comment1.getAuthor();
        Item item1 = comment1.getItem();
        Booking booking1 = item1.getLastBooking();
        Booking booking2 = item1.getNextBooking();
        List<CommentDto> comments = item1.getComments();
        comment1.setCreated(LocalDateTime.now());

        assertThrows(ValidationException.class, () -> itemController.createComment(comment, item.getId(), user2.getId()));
    }

    @Test
    void emptyComment() {
        comment.setText(" ");
        assertThrows(ConstraintViolationException.class, () -> itemController.createComment(comment, 1L, 1L));
    }

    @Test
    void createCommentByWrongUser() {
        assertThrows(ValidationException.class, () -> itemController.createComment(comment, 1L, 1L));
    }

    @Test
    void createCommentToWrongItem() {
        userController.create(userDto);
        assertThrows(ValidationException.class, () -> itemController.createComment(comment, 1L, 1L));
        itemController.createItem(Optional.of(1L), itemDto);
        assertThrows(ValidationException.class, () -> itemController.createComment(comment, 3L, 1L));

    }

    @Test
    void getAllWithWrongFrom() {
        assertThrows(NoSuchElementException.class, () -> itemController.getItems(Optional.of(1L)));
    }

}
