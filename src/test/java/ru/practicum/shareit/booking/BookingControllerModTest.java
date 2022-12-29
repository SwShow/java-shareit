package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
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
    UserService userService;

    @Autowired
    private ItemController itemController;

    UserDto  userDto = new UserDto(0L, "new", "new@mail.com");

    ItemDto itemDto = new ItemDto(0L, "new", "description", true, null, null,
            new ArrayList<>(), 0L);
    private UserDto userDto1;

    private BookingDto bookingDto = new BookingDto(0L, 1L,
            LocalDateTime.of(2022, 12, 30, 12, 30),
            LocalDateTime.of(2023, 11, 10, 13, 0),
            itemDto, userDto, WAITING);

    @Test
    void shouldCreateTest() {

        userDto = new UserDto();
        userDto.setName("aname");
        userDto.setEmail("auser@email.com");
        UserDto user = userController.create(userDto).getBody();
        ItemDto itemDto = new ItemDto(0L, "name", "description", true, null, null,
                new ArrayList<>(), 0L);
        itemController.createItem(Optional.of(user.getId()), itemDto);
        userDto1 = new UserDto();
        userDto1.setName("name");
        userDto1.setEmail("user1@email.com");
        UserDto user1 = userController.create(userDto1).getBody();
        BookingDto booking = bookingController.save(bookingDto, user1.getId()).getBody();
        assertEquals(1L, bookingController.getById(booking.getId(), user1.getId()).getBody().getId());

    }

    @Test
    void createByWrongUserTest() {
        assertThrows(NoSuchElementException.class, () -> bookingController.save(bookingDto, 1L));
    }

    @Test
    void createForWrongItemTest() {
        userDto = new UserDto();
        userDto.setName("bname");
        userDto.setEmail("buser@email.com");
        UserDto user = userController.create(userDto).getBody();
        assertThrows(NoSuchElementException.class, () -> bookingController.save(bookingDto, 1L));

    }

    @Test
    void createByOwnerTest() {
        userDto = new UserDto();
        userDto.setName("cname");
        userDto.setEmail("cuser@email.com");
        UserDto user = userController.create(userDto).getBody();
        assert user != null;
        long id = user.getId();
        System.out.println("id" + id);
        ItemDto itemDto = new ItemDto(0L, "aname", "description", true, null, null,
                new ArrayList<>(), 0L);
        itemController.createItem(Optional.of(id), itemDto);
        assertThrows(NoSuchElementException.class, () -> bookingController.save(bookingDto, 1L));

    }

    @Test
    void createToUnavailableItemTest() {
        userDto = new UserDto();
        userDto.setName("dname");
        userDto.setEmail("duser@email.com");
        UserDto user = userController.create(userDto).getBody();
        ItemDto itemDto = new ItemDto(0L, "bname", "description", true, null, null,
                new ArrayList<>(), 0L);
        itemDto.setAvailable(false);
        itemController.createItem(Optional.of(user.getId()), itemDto);
        userDto1 = new UserDto();
        userDto1.setName("name");
        userDto1.setEmail("user1@email.com");
        UserDto user1 = userController.create(userDto1).getBody();
        assertThrows(ValidationException.class, () -> bookingController.save(bookingDto, 2L));

    }

    @Test
    void createWithWrongEndDate() {
        userDto = new UserDto();
        userDto.setName("ename");
        userDto.setEmail("euser@email.com");
        UserDto user = userController.create(userDto).getBody();
        ItemDto itemDto = new ItemDto(0L, "cname", "description", true, null, null,
                new ArrayList<>(), 0L);
        itemController.createItem(Optional.of(user.getId()), itemDto);
        userDto1 = new UserDto();
        userDto1.setName("gname");
        userDto1.setEmail("guser1@email.com");
        UserDto user1 = userController.create(userDto1).getBody();
        bookingDto.setEnd(LocalDateTime.of(2022, 9, 24, 12, 30));
        assertThrows(ValidationException.class, () -> bookingController.save(bookingDto, user1.getId()));

    }

    @Test
    void approveTest() {
        userDto = new UserDto();
        userDto.setName("fname");
        userDto.setEmail("fuser@email.com");
        UserDto user = userController.create(userDto).getBody();
        ItemDto itemDto = new ItemDto(0L, "dname", "description", true, null, null,
                new ArrayList<>(), 0L);
        itemController.createItem(Optional.of(user.getId()), itemDto);
        userDto1 = new UserDto();
        userDto1.setName("hname");
        userDto1.setEmail("huser1@email.com");
        UserDto user1 = userController.create(userDto1).getBody();
        bookingDto = new BookingDto(0L, 1L,
                LocalDateTime.of(2022, 12, 30, 12, 30),
                LocalDateTime.of(2023, 11, 10, 13, 0),
                itemDto, userDto1, WAITING);
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
        userDto = new UserDto();
        userDto.setName("iname");
        userDto.setEmail("iuser@email.com");
        ResponseEntity<UserDto>  res = userController.create(userDto);
        UserDto user = res.getBody();
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        ItemDto itemDto = new ItemDto(0L, "ename", "description", true, null, null,
                new ArrayList<>(), 0L);
        itemController.createItem(Optional.of(user.getId()), itemDto);
        userDto1 = new UserDto();
        userDto1.setName("jname");
        userDto1.setEmail("juser1@email.com");
        UserDto user1 = userController.create(userDto1).getBody();
        bookingController.save(bookingDto, user1.getId());
        assertThrows(NoSuchElementException.class, () -> bookingController.approve(1L, true, 2L));

    }

    @Test
    void approveBookingWithWrongStatus() {
        userDto = new UserDto();
        userDto.setName("kname");
        userDto.setEmail("kuser@email.com");
        UserDto user = userController.create(userDto).getBody();
        ItemDto itemDto = new ItemDto(0L, "fname", "description", true, null, null,
                new ArrayList<>(), 0L);
        itemController.createItem(Optional.of(user.getId()), itemDto);
        userDto1 = new UserDto();
        userDto1.setName("lname");
        userDto1.setEmail("luser1@email.com");
        UserDto user1 = userController.create(userDto1).getBody();
        bookingController.save(bookingDto, user1.getId());
        bookingController.approve(1L, true, 1L);
        assertThrows(ValidationException.class, () -> bookingController.approve(1L, true, 1L));

    }

    @Test
    void getAllUserTest() {
        userDto = new UserDto();
        userDto.setName("mname");
        userDto.setEmail("muser@email.com");
        UserDto user = userController.create(userDto).getBody();
        assert user != null;
        ItemDto itemDto = new ItemDto(0L, "gname", "description", true, null, null,
                new ArrayList<>(), 0L);
        itemController.createItem(Optional.of(user.getId()), itemDto);
        userDto1 = new UserDto();
        userDto1.setName("oname");
        userDto1.setEmail("ouser1@email.com");
        UserDto user1 = userController.create(userDto1).getBody();
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
        userDto = new UserDto();
        userDto.setName("pname");
        userDto.setEmail("puser@email.com");
        UserDto user = userController.create(userDto).getBody();
        System.out.println(user);
        assert user != null;
        ItemDto itemDto = new ItemDto(0L, "iname", "description", true, null, null,
                new ArrayList<>(), 0L);
        itemController.createItem(Optional.of(user.getId()), itemDto);
        userDto1 = new UserDto();
        userDto1.setName("qname");
        userDto1.setEmail("quser1@email.com");
        UserDto user1 = userController.create(userDto1).getBody();
        assert user1 != null;
        bookingController.save(bookingDto, user1.getId());
        assertThrows(NoSuchElementException.class, () -> bookingController.getById(1L, 10L));
    }

    @Test
    void findBookingsForUserWithoutItems() {
        userDto = new UserDto();
        userDto.setName("zname");
        userDto.setEmail("zuser@email.com");
        UserDto user = userController.create(userDto).getBody();
        assertThrows(ValidationException.class, () -> bookingController.getAllForOwner(user.getId(),  "ALL",
                0, 20));

    }

    @Test
    void  findBookingsWithUnsupportedStatus() {
        userDto = new UserDto();
        userDto.setName("sname");
        userDto.setEmail("suser@email.com");
        UserDto user = userController.create(userDto).getBody();
        assertThrows(ValidationException.class, () -> bookingController.getAllForOwner(user.getId(), "HI",
                0, 20));
    }
}