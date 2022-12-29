package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingControllerModTest {
    @Autowired
    private BookingController bookingController;
    @Autowired
    private UserController userController;
    @Autowired
    private ItemController itemController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    UserDto userDto;
    ItemDto itemDto;
    private UserDto userDto1;
    private BookingDto bookingDto;

    @BeforeEach
    void beforeEach() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
        userDto = new UserDto(0L, "new", "new@mail.com");

        userDto1 = new UserDto(0L, "name", "user1@email.com");

        itemDto = new ItemDto(0L, "new", "description", true, null, null,
                new ArrayList<>(), 0L);

        bookingDto = new BookingDto(0L, 1L,
                LocalDateTime.of(2022, 12, 30, 12, 30),
                LocalDateTime.of(2023, 11, 10, 13, 0),
                itemDto, userDto, WAITING);
    }

    @Test
    void shouldCreateTest() {
        UserDto user = userController.create(userDto).getBody();
        ItemDto item = itemController.createItem(Optional.of(user.getId()), itemDto).getBody();
        UserDto user1 = userController.create(userDto1).getBody();
        bookingDto.setItemId(item.getId());
        BookingDto booking = bookingController.save(bookingDto, user1.getId()).getBody();
        assertEquals(1L, bookingController.getById(booking.getId(), user1.getId()).getBody().getId());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(NoSuchElementException.class, () -> bookingController.save(bookingDto, 1L));
    }

    @Test
    void createForWrongItemTest() {
        userController.create(userDto).getBody();
        assertThrows(NoSuchElementException.class, () -> bookingController.save(bookingDto, 1L));

    }

    @Test
    void createByOwnerTest() {
        UserDto user = userController.create(userDto).getBody();
        assert user != null;
        long id = user.getId();
        System.out.println("id" + id);
        itemController.createItem(Optional.of(id), itemDto);
        assertThrows(NoSuchElementException.class, () -> bookingController.save(bookingDto, 1L));

    }

    @Test
    void createToUnavailableItemTest() {
        UserDto user = userController.create(userDto).getBody();
        itemDto.setAvailable(false);
        itemController.createItem(Optional.of(user.getId()), itemDto);
        UserDto user1 = userController.create(userDto1).getBody();
        assertThrows(NoSuchElementException.class, () -> bookingController.save(bookingDto, 2L));
    }

    @Test
    void createWithWrongEndDate() {
        UserDto user = userController.create(userDto).getBody();
        itemController.createItem(Optional.of(user.getId()), itemDto);
        UserDto user1 = userController.create(userDto1).getBody();
        bookingDto.setEnd(LocalDateTime.of(2022, 9, 24, 12, 30));
        assertThrows(NoSuchElementException.class, () -> bookingController.save(bookingDto, user1.getId()));
    }

    @Test
    void approveTest() {
        UserDto user = userController.create(userDto).getBody();
        ItemDto item = itemController.createItem(Optional.of(user.getId()), itemDto).getBody();
        UserDto user1 = userController.create(userDto1).getBody();
        bookingDto.setItemId(item.getId());
        BookingDto booking = bookingController.save(bookingDto, user1.getId()).getBody();
        assertEquals(WAITING, bookingController.getById(booking.getId(), user1.getId()).getBody().getStatus());
        bookingController.approve(booking.getId(), true, user.getId());
        assertEquals(APPROVED, bookingController.getById(booking.getId(), user1.getId()).getBody().getStatus());

    }

    @Test
    void approveWrongBookingTest() {
        assertThrows(NoSuchElementException.class, () -> bookingController.approve(1L, true, 1L));
    }

    @Test
    void approveWrongUserTest() {
        ResponseEntity<UserDto> res = userController.create(userDto);
        UserDto user = res.getBody();
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        ItemDto item = itemController.createItem(Optional.of(user.getId()), itemDto).getBody();
        UserDto user1 = userController.create(userDto1).getBody();
        bookingDto.setItemId(item.getId());
        bookingController.save(bookingDto, user1.getId());
        assertThrows(NoSuchElementException.class, () -> bookingController.approve(1L, true, 2L));

    }

    @Test
    void approveBookingWithWrongStatus() {
        UserDto user = userController.create(userDto).getBody();
        ItemDto item = itemController.createItem(Optional.of(user.getId()), itemDto).getBody();
        UserDto user1 = userController.create(userDto1).getBody();
        bookingDto.setItemId(item.getId());
        bookingController.save(bookingDto, user1.getId());
        bookingController.approve(1L, true, 1L);
        assertThrows(ValidationException.class, () -> bookingController.approve(1L, true, 1L));

    }

    @Test
    void getAllUserTest() {
        UserDto user = userController.create(userDto).getBody();
        assert user != null;
        ItemDto item = itemController.createItem(Optional.of(user.getId()), itemDto).getBody();
        UserDto user1 = userController.create(userDto1).getBody();
        bookingDto.setItemId(item.getId());
        BookingDto booking = bookingController.save(bookingDto, user1.getId()).getBody();
        assertEquals(1, bookingController.getAllForBooker(user1.getId(), "WAITING",
                0, 10).getBody().size());
        assertEquals(1, bookingController.getAllForBooker(user1.getId(), "ALL",
                0, 10).getBody().size());
        assertEquals(0, bookingController.getAllForBooker(user1.getId(), "PAST",
                0, 10).getBody().size());
        assertEquals(0, bookingController.getAllForBooker(user1.getId(), "CURRENT",
                0, 10).getBody().size());
        assertEquals(1, bookingController.getAllForBooker(user1.getId(), "FUTURE",
                0, 10).getBody().size());
        assertEquals(0, bookingController.getAllForBooker(user1.getId(), "REJECTED",
                0, 10).getBody().size());
        bookingController.approve(booking.getId(), true, user.getId());
        assertEquals(0, bookingController.getAllForOwner(user.getId(), "CURRENT",
                0, 10).getBody().size());
        assertEquals(1, bookingController.getAllForOwner(user.getId(), "ALL",
                0, 10).getBody().size());
        assertEquals(0, bookingController.getAllForOwner(user.getId(), "WAITING",
                0, 10).getBody().size());
        assertEquals(1, bookingController.getAllForOwner(user.getId(), "FUTURE",
                0, 10).getBody().size());
        assertEquals(0, bookingController.getAllForOwner(user.getId(), "REJECTED",
                0, 10).getBody().size());
        assertEquals(0, bookingController.getAllForOwner(user.getId(), "PAST",
                0, 10).getBody().size());

    }

    @Test
    void getAllWrongUserTest() {
        assertThrows(NoSuchElementException.class, () -> bookingController.getAllForBooker(1L, "ALL",
                0, 10));
        assertThrows(NoSuchElementException.class, () -> bookingController.getAllForOwner(1L, "ALL",
                0, 10));
    }

    @Test
    void getWrongIdTest() {
        assertThrows(NoSuchElementException.class, () -> bookingController.getById(1L, 1L));
    }

    @Test
    void getWrongUser() {
        UserDto user = userController.create(userDto).getBody();
        System.out.println(user);
        assert user != null;
        ItemDto item = itemController.createItem(Optional.of(user.getId()), itemDto).getBody();
        UserDto user1 = userController.create(userDto1).getBody();
        assert user1 != null;
        bookingDto.setItemId(item.getId());
        bookingController.save(bookingDto, user1.getId());
        assertThrows(NoSuchElementException.class, () -> bookingController.getById(1L, 10L));
    }

    @Test
    void findBookingsForUserWithoutItems() {
        UserDto user = userController.create(userDto).getBody();
        assertThrows(ValidationException.class, () -> bookingController.getAllForOwner(user.getId(), "ALL",
                0, 20));

    }

    @Test
    void findBookingsWithUnsupportedStatus() {
        UserDto user = userController.create(userDto).getBody();
        assertThrows(ValidationException.class, () -> bookingController.getAllForOwner(user.getId(), "HI",
                0, 20));
    }
}