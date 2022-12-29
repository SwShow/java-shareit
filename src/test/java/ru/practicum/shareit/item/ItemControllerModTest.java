package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoLittle;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemControllerModTest {
    @Autowired
    private ItemController itemController;
    @Autowired
    private UserController userController;
    @Autowired
    private ItemRequestController requestController;
    @Autowired
    private BookingController bookingController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    private ItemDto itemDto;
    private UserDto userDto;
    private ItemRequestDto requestDto;
    private CommentDtoLittle comment;

    @BeforeEach
    void beforeEach() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        itemDto = new ItemDto(0L, "name", "description", true, null,
                null, new ArrayList<>(), 0L);

        userDto = new UserDto(0L, "name", "user@email.com");

        requestDto = new ItemRequestDto(0L, "item request description", null, new ArrayList<>());

        comment = new CommentDtoLittle("first comment", null, null);
    }

    @Test
    void createTest() {
        UserDto user = userController.create(userDto).getBody();
        ItemDto item = itemController.createItem(Optional.of(1L), itemDto).getBody();
        assertEquals(item.getId(), itemController.getItemOfId(user.getId(), item.getId()).getBody().getId());
    }

    @Test
    void createWithRequestTest() {
        UserDto user = userController.create(userDto).getBody();
        assert user != null;
        requestController.addItemRequest(user.getId(), requestDto);
        itemDto.setRequestId(1L);
        userController.create(new UserDto(0L, "name", "user1@email.com"));
        ResponseEntity<ItemDto> res = itemController.createItem(Optional.of(2L), itemDto);
        ItemDto item = res.getBody();
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assert item != null;
        assertEquals(item.getName(), itemController.getItemOfId(2L, 1L).getBody().getName());
    }

    @Test
    void createByWrongUser() {
        assertThrows(NoSuchElementException.class, () -> itemController.createItem(
                Optional.of(1L), itemDto));
    }

    @Test
    void createWithWrongItemRequest() {
        itemDto.setRequestId(10L);
        userController.create(userDto);
        assertThrows(NoSuchElementException.class, () -> itemController.createItem(
                Optional.of(1L), itemDto));
    }

    @Test
    void getAllUserItems() {
        UserDto user = userController.create(userDto).getBody();
        ItemDto item = itemController.createItem(Optional.of(1L), itemDto).getBody();
        assertEquals(1, itemController.getItems(Optional.of(user.getId())).getBody().size());
    }

    @Test
    void updateTest() {
        userController.create(userDto);
        itemController.createItem(Optional.of(1L), itemDto);
        ItemDto item = new ItemDto(0L, "new name", "updateDescription", false, null,
                null, new ArrayList<>(), 0L);
        itemController.updateItem(Optional.of(1L), 1L, item);
        assertEquals(item.getDescription(), itemController.getItemOfId(1L, 1L).getBody().getDescription());
    }

    @Test
    void updateForWrongItemTest() {
        assertThrows(NoSuchElementException.class, () -> itemController.updateItem(Optional.of(1L), 1L, itemDto));
    }

    @Test
    public void updateItemWithoutId() {
        Assertions.assertThrows(ValidationException.class, () -> itemController.updateItem(Optional.of(-1L), 1L, itemDto));
    }

    @Test
    void updateByWrongUserTest() {
        userController.create(userDto);
        itemController.createItem(Optional.of(1L), itemDto);
        itemDto.setName("new name");
        assertThrows(NoSuchElementException.class, () -> itemController.updateItem(
                Optional.of(1L), 10L, itemDto));
    }

    @Test
    void searchTest() {
        userController.create(userDto);
        itemController.createItem(Optional.of(1L), itemDto);
        assertEquals(1, itemController.getItemOfText(Optional.of(1L), "Desc").getBody().size());
    }

    @Test
    void searchEmptyTextTest() {
        userController.create(userDto);
        itemController.createItem(Optional.of(1L), itemDto);
        assertEquals(new ArrayList<ItemDto>(), itemController.getItemOfText(Optional.of(1L), "").getBody());
    }

    @Test
    void createCommentTest() {
        Comment newComment = new Comment(0L, "comment", itemMapper.toItem(itemDto),
                userMapper.toUser(userDto), LocalDateTime.now());
        CommentDto newCommentDto = commentMapper.toCommentDto(newComment);
        newCommentDto.setAuthorName(userDto.getName());
        UserDto user = userController.create(userDto).getBody();
        ItemDto item = itemController.createItem(Optional.of(1L), itemDto).getBody();
        assert item != null;
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
                itemDto, user2, WAITING), user2.getId()).getBody();
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
    void createEmptyComment() {
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