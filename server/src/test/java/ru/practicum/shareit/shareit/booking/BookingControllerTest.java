
package ru.practicum.shareit.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.shareit.booking.BookingStatus.WAITING;


@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
  @InjectMocks
    private BookingController bookingController;
    @Mock
    private BookingService bookingService;

    private final BookingDto bookingDto1 = new BookingDto(0L, 1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            null, null, WAITING);

    private final BookingDto bookingDto2 = new BookingDto(1L, 1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
            null, null, APPROVED);

    @Test
    public void save() {
        when(bookingService.save(bookingDto1, 1L))
                .thenReturn(bookingDto2);

        BookingDto res = bookingController.save(bookingDto1, 1L);
        assertEquals(res.getId(), 1L);
    }

    @Test
    public void approve() {
        when(bookingService.approve(1L, 1L, true))
                .thenReturn(bookingDto2);

        BookingDto res = bookingController.approve(1L, true,1L);
        assertEquals(res.getStatus(), APPROVED);
    }

    @Test
    public void getById() {
        when(bookingService.getById(1L, 1L))
                .thenReturn(bookingDto2);

        BookingDto res = bookingController.getById(1L, 1L);
        assertEquals(res.getId(), 1L);
    }

    @Test
    void getAllForBooker() {
        when(bookingService.findAllForBooker(0, 5, 1L, "WAITING"))
                .thenReturn(List.of(bookingDto2));

        List<BookingDto> res = bookingController.getAllForBooker(1L, "WAITING", 0, 5);
        assertEquals(res.size(), 1);
    }

    @Test
    void getAllForOwner() {
        when(bookingService.findAllForOwner(0, 5, 1L, "WAITING"))
                .thenReturn(List.of(bookingDto2));

        List<BookingDto> res = bookingController.getAllForOwner(1L, "WAITING", 0, 5);
        assertEquals(res.size(), 1);
    }
}
