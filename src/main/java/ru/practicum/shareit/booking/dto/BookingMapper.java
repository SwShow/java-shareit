package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;


import java.util.ArrayList;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem(), null, null, new ArrayList<>()),
                UserMapper.INSTANCE.toDto(booking.getBooker()),
                booking.getStatus());
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                BookingStatus.WAITING);
    }
}