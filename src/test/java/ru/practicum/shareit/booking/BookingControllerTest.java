package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.BookingDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {
  @InjectMocks
    BookingController bookingController;
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

        ResponseEntity<BookingDto> res = bookingController.save(bookingDto1, 1L);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(res.getBody()).getId()).isEqualTo(1);
    }

    @Test
    public void approve() {
        when(bookingService.approve(1L, 1L, true))
                .thenReturn(bookingDto2);

        ResponseEntity<BookingDto> res = bookingController.approve(1L, true,1L);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(res.getBody()).getStatus()).isEqualTo(APPROVED);
    }

    @Test
    public void getById() {
        when(bookingService.getById(1L, 1L))
                .thenReturn(bookingDto2);

        ResponseEntity<BookingDto> res = bookingController.getById(1L, 1L);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(res.getBody()).getId()).isEqualTo(1L);
    }

    @Test
    void getAllForBooker() {
        when(bookingService.findAllForBooker(PageRequest.of(0 / 5, 5), 1L, "WAITING"))
                .thenReturn(List.of(bookingDto2));

        ResponseEntity<List<BookingDto>> res = bookingController.getAllForBooker(1L, "WAITING", 0, 5);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(res.getBody()).size()).isEqualTo(1);
    }

    @Test
    void getAllForOwner() {
        when(bookingService.findAllForOwner(PageRequest.of(0 / 5, 5), 1L, "WAITING"))
                .thenReturn(List.of(bookingDto2));

        ResponseEntity<List<BookingDto>> res = bookingController.getAllForOwner(1L, "WAITING", 0, 5);
        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(res.getBody()).size()).isEqualTo(1);
    }
}