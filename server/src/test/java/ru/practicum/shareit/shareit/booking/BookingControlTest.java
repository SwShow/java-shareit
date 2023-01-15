package ru.practicum.shareit.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.shareit.exception.BadRequestException;
import ru.practicum.shareit.shareit.exception.NotFoundException;
import ru.practicum.shareit.shareit.item.ItemController;
import ru.practicum.shareit.shareit.item.ItemRepository;
import ru.practicum.shareit.shareit.item.dto.ItemDto;
import ru.practicum.shareit.shareit.user.UserController;
import ru.practicum.shareit.shareit.user.UserRepository;
import ru.practicum.shareit.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControlTest {

    private final BookingController bookingController;
    private final UserController userController;
    private final ItemController itemController;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private UserDto userDto;
    private ItemDto itemDto;
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
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(7),
                itemDto, userDto, BookingStatus.WAITING);
    }

    @Test
    void shouldCreateTest() {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.createItem(user.getId(), itemDto);
        UserDto user1 = userController.create(userDto1);
        bookingDto.setItemId(item.getId());
        BookingDto booking = bookingController.save(bookingDto, user1.getId());
        Assertions.assertEquals(booking.getId(), bookingController.getById(booking.getId(), user1.getId()).getId());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(NotFoundException.class, () -> bookingController.save(bookingDto, 1L));
    }

    @Test
    void createForWrongItemTest() {
        userController.create(userDto);
        assertThrows(NotFoundException.class, () -> bookingController.save(bookingDto, 1L));

    }

    @Test
    void createByOwnerTest() {
        UserDto user = userController.create(userDto);
        assert user != null;
        long id = user.getId();
        System.out.println("id" + id);
        itemController.createItem(id, itemDto);
        assertThrows(NotFoundException.class, () -> bookingController.save(bookingDto, 1L));

    }

    @Test
    void createToUnavailableItemTest() {
        UserDto user = userController.create(userDto);
        itemDto.setAvailable(false);
        itemController.createItem(user.getId(), itemDto);
        UserDto user1 = userController.create(userDto1);
        assertThrows(NotFoundException.class, () -> bookingController.save(bookingDto, 2L));
    }

    @Test
    void createWithWrongEndDate() {
        UserDto user = userController.create(userDto);
        itemController.createItem(user.getId(), itemDto);
        UserDto user1 = userController.create(userDto1);
        bookingDto.setEnd(LocalDateTime.now().minusDays(7));
        assertThrows(NotFoundException.class, () -> bookingController.save(bookingDto, user1.getId()));
    }

    @Test
    void approveTest() {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.createItem(user.getId(), itemDto);
        UserDto user1 = userController.create(userDto1);
        bookingDto.setItemId(item.getId());
        BookingDto booking = bookingController.save(bookingDto, user1.getId());
        Assertions.assertEquals(BookingStatus.WAITING, bookingController.getById(booking.getId(), user1.getId()).getStatus());
        bookingController.approve(booking.getId(), true, user.getId());
        Assertions.assertEquals(BookingStatus.APPROVED, bookingController.getById(booking.getId(), user1.getId()).getStatus());

    }

    @Test
    void approveWrongBookingTest() {
        assertThrows(NotFoundException.class, () -> bookingController.approve(1L, true, 1L));
    }

    @Test
    void approveWrongUserTest() {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.createItem(user.getId(), itemDto);
        UserDto user1 = userController.create(userDto1);
        bookingDto.setItemId(item.getId());
        bookingController.save(bookingDto, user1.getId());
        assertThrows(NotFoundException.class, () -> bookingController.approve(1L, true, 5L));

    }

    @Test
    void approveBookingWithWrongStatus() {
        UserDto user = userController.create(userDto);
        ItemDto item = itemController.createItem(user.getId(), itemDto);
        UserDto user1 = userController.create(userDto1);
        bookingDto.setItemId(item.getId());
        BookingDto booking = bookingController.save(bookingDto, user1.getId());
        bookingController.approve(booking.getId(), true, user.getId());
        assertThrows(BadRequestException.class, () -> bookingController.approve(booking.getId(), true, user.getId()));

    }

    @Test
    void getAllUserTest() {
        UserDto user = userController.create(userDto);
        assert user != null;
        ItemDto item = itemController.createItem(user.getId(), itemDto);
        UserDto user1 = userController.create(userDto1);
        bookingDto.setItemId(item.getId());
        BookingDto booking = bookingController.save(bookingDto, user1.getId());
        assertEquals(1, bookingController.getAllForBooker(user1.getId(), "WAITING",
                0, 10).size());
        assertEquals(1, bookingController.getAllForBooker(user1.getId(), "ALL",
                0, 10).size());
        assertEquals(0, bookingController.getAllForBooker(user1.getId(), "PAST",
                0, 10).size());
        assertEquals(0, bookingController.getAllForBooker(user1.getId(), "CURRENT",
                0, 10).size());
        assertEquals(1, bookingController.getAllForBooker(user1.getId(), "FUTURE",
                0, 10).size());
        assertEquals(0, bookingController.getAllForBooker(user1.getId(), "REJECTED",
                0, 10).size());
        bookingController.approve(booking.getId(), true, user.getId());
        assertEquals(0, bookingController.getAllForOwner(user.getId(), "CURRENT",
                0, 10).size());
        assertEquals(1, bookingController.getAllForOwner(user.getId(), "ALL",
                0, 10).size());
        assertEquals(0, bookingController.getAllForOwner(user.getId(), "WAITING",
                0, 10).size());
        assertEquals(1, bookingController.getAllForOwner(user.getId(), "FUTURE",
                0, 10).size());
        assertEquals(0, bookingController.getAllForOwner(user.getId(), "REJECTED",
                0, 10).size());
        assertEquals(0, bookingController.getAllForOwner(user.getId(), "PAST",
                0, 10).size());

    }

    @Test
    void getAllWrongUserTest() {
        assertThrows(NotFoundException.class, () -> bookingController.getAllForBooker(1L, "ALL",
                0, 10));
        assertThrows(NotFoundException.class, () -> bookingController.getAllForOwner(1L, "ALL",
                0, 10));
    }

    @Test
    void getWrongIdTest() {
        assertThrows(NotFoundException.class, () -> bookingController.getById(1L, 1L));
    }

    @Test
    void getWrongUser() {
        UserDto user = userController.create(userDto);
        System.out.println(user);
        assert user != null;
        ItemDto item = itemController.createItem(user.getId(), itemDto);
        UserDto user1 = userController.create(userDto1);
        assert user1 != null;
        bookingDto.setItemId(item.getId());
        bookingController.save(bookingDto, user1.getId());
        assertThrows(BadRequestException.class, () -> bookingController.getById(1L, 10L));
    }

    @Test
    void findBookingsForUserWithoutItems() {
        UserDto user = userController.create(userDto);
        assertThrows(BadRequestException.class, () -> bookingController.getAllForOwner(user.getId(), "ALL",
                0, 20));

    }

    @Test
    void findBookingsWithUnsupportedStatus() {
        UserDto user = userController.create(userDto);
        assertThrows(BadRequestException.class, () -> bookingController.getAllForOwner(user.getId(), "HI",
                0, 20));
    }

}