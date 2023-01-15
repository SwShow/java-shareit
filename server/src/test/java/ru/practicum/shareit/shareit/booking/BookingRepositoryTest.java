package ru.practicum.shareit.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.shareit.booking.model.Booking;
import ru.practicum.shareit.shareit.item.ItemRepository;
import ru.practicum.shareit.shareit.item.model.Item;
import ru.practicum.shareit.shareit.user.UserRepository;
import ru.practicum.shareit.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.shareit.booking.BookingStatus.WAITING;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private User user;
    private Item item;
    private User user2;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        user = new User(0L, "name", "user@email.com");

        item = new Item(0L, "name", "description", true, user, null);

        user2 = new User(0L, "name2", "email2@email.com");

        booking = new Booking();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(7));
        booking.setStatus(WAITING);
        booking.setBooker(user2);
        booking.setItem(item);
    }

    @Test
    void findCurrentOwnerBookings() {
        User userSaved = userRepository.save(user);
        itemRepository.save(item);
        User userSaved2 = userRepository.save(user2);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findCurrentOwnerBookings(userSaved.getId(), LocalDateTime.now()).size(),
                equalTo(1));
    }

    @Test
    void findCurrentBookerBookings() {
        User userSaved = userRepository.save(user);
        itemRepository.save(item);
        User userSaved2 = userRepository.save(user2);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findCurrentBookerBookings(userSaved2.getId(), LocalDateTime.now()).size(),
                equalTo(1));
    }

    @Test
    void findPastOwnerBookings() {
        User userSaved = userRepository.save(user);
        Item itemSaved = itemRepository.save(item);
        User userSaved2 = userRepository.save(user2);
        bookingRepository.save(booking);

        assertThat(bookingRepository.findPastOwnerBookings(itemSaved.getId(), userSaved.getId(),
                        LocalDateTime.now()).size(),
                equalTo(0));
    }

    @Test
    void findFutureOwnerBookings() {
        User userSaved = userRepository.save(user);
        item.setOwner(userSaved);
        Item itemSaved = itemRepository.save(item);
        User userSaved2 = userRepository.save(user2);
        booking.setStart(LocalDateTime.now().plusDays(1));
        bookingRepository.save(booking);

        assertThat(bookingRepository.findFutureOwnerBookings(itemSaved.getId(), userSaved.getId(),
                        LocalDateTime.now()).size(),
                equalTo(1));
    }

}