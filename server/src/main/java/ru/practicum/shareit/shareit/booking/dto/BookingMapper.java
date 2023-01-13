package ru.practicum.shareit.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.shareit.booking.model.Booking;

@Mapper
public interface BookingMapper {

    @Mapping(target = "itemId", ignore = true)
    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "booker", ignore = true)
    Booking toBooking(BookingDto bookingDto);
}
