package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

@Mapper
public interface ItemMapper {
    @Mapping(target = "requestId", source = "request.id")
    ItemDto toItemDto(Item item);

    @Mapping(target = "request", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Item toItem(ItemDto item);

    @Mapping(target = "bookerId", source = "booker.id")
    ItemDto.BookingForItemDto bookingToBookingForItemDto(Booking booking);

}
