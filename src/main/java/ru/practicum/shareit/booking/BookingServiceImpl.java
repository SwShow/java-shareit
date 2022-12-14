package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.*;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto save(BookingDto bookingDto, long userId) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NoSuchElementException("вещь с id " + bookingDto.getItemId() + " не найдена"));
        User booker = getUser(userId);
        if (item.getOwner().getId() == userId) {
            throw new NoSuchElementException("это ваша вещь");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("время бронирования указано не корректно");
        }
        bookingDto.setStatus(WAITING);
        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(booker);
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("вещь не доступна для бронирования");
        }

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(long userId, long bookingId, boolean status) {
        getUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("указанное бронирование не существует"));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NoSuchElementException("подтверждение бронирования может быть выполнено только владельцем вещи");
        }
        if (booking.getStatus().equals(APPROVED)) {
            throw new ValidationException("вы уже подтвердили бронирование");
        }
        booking.setStatus(status ? APPROVED : REJECTED);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(long bookingId, long userId) {
        getUser(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("указанное бронирование не существует"));
        long id = booking.getItem().getId();
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("вещь с id " + id + " не найдена."));
        if (booking.getBooker().getId() != userId) {
            if (item.getOwner().getId() != userId) {
                throw new NoSuchElementException("вещь забронирована не вами");
            }
        }

        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllForBooker(long bookerId, String state) {
        getUser(bookerId);

        return findBookingsForBooking(state, bookerId)
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllForOwner(long ownerId, String state) {
        getUser(ownerId);
        if (itemRepository.findAllByOwnerId(ownerId).isEmpty()) {
            throw new ValidationException("у вас нет вещей");
        }

        return findBookingsForOwner(state, ownerId)
                .stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("пользователь c идентификатором " + userId + " не существует."));
    }

    public List<Booking> findBookingsForOwner(String state, long id) {
        List<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(id);
                return bookings;
            case "WAITING":
            case "APPROVED":
            case "REJECTED":
            case "CANCELED":
                BookingStatus status = null;

                for (BookingStatus value : BookingStatus.values()) {
                    if (value.name().equals(state)) {
                        status = value;
                    }
                }
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(id, status);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(id, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(id, LocalDateTime.now());
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentOwnerBookings(id, LocalDateTime.now());
                break;

            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    public List<Booking> findBookingsForBooking(String state, long id) {
        List<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(id);
                return bookings;
            case "WAITING":
            case "APPROVED":
            case "REJECTED":
            case "CANCELED":
                BookingStatus status = null;
                for (BookingStatus value : BookingStatus.values()) {
                    if (value.name().equals(state)) {
                        status = value;
                    }
                }
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(id, status);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(id, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(id, LocalDateTime.now());
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookerBookings(id, LocalDateTime.now());
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }
}