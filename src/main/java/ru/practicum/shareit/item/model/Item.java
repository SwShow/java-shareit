package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.persistence.Table;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long id;
    @Column
    private String name;
    private String description;
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "requests_id", referencedColumnName = "id")
    private ItemRequest request;

    @Transient
    private Booking lastBooking;
    @Transient
    private Booking nextBooking;
    @Transient
    private List<CommentDto> comments;


}
