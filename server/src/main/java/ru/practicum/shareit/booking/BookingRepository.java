package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime now);

    Long countAllByItemIdAndBookerIdAndEndBefore(long itemId, long userId, LocalDateTime now);

    List<Booking> findAllByItemIdAndStartBeforeOrderByStartDesc(long itemId, LocalDateTime now);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartDesc(long itemId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and " +
            "b.end < ?3 order by b.start desc")
    List<Booking> findPastOwnerBookings(long itemId, long ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and " +
            "b.start > ?3 " +
            "order by b.start desc")
    List<Booking> findFutureOwnerBookings(long itemId, long ownerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findCurrentBookerBookings(long bookerId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1 and " +
            "b.start < ?2 and " +
            "b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findCurrentOwnerBookings(long ownerId, LocalDateTime now);
}