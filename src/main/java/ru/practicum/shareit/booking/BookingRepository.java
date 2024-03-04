package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime start);

    @Query("select b from Booking b " +
            "where b.booker.id = :userId and b.start < :start and b.end > :end " +
            "order by b.start desc")
    List<Booking> findAllByBookerIdAndCurrent(
            @Param("userId") long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, Booking.Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long userId, Booking.Status status);

    Optional<Booking> findFirstByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(
            long itemId, LocalDateTime end, Booking.Status status);

    Optional<Booking> findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(
            long itemId, LocalDateTime start, Booking.Status status);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusIsAndEndIsBeforeOrderByEndDesc(
            long userId, long itemId, Booking.Status status, LocalDateTime end);

}
