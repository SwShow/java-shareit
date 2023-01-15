package ru.practicum.shareit.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import ru.practicum.shareit.shareit.booking.BookingStatus;
import ru.practicum.shareit.shareit.item.dto.ItemDto;
import ru.practicum.shareit.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookingDto {
    private long id;
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;
}
