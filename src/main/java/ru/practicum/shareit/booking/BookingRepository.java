package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBefore(long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfter(long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
            long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(long userId, Booking.Status status, Pageable pageable);

    List<Booking> findAllByItemOwnerId(long userId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
            long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatus(long userId, Booking.Status status, Pageable pageable);

    Optional<Booking> findFirstByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(
            long itemId, LocalDateTime end, Booking.Status status);

    Optional<Booking> findFirstByItemIdAndStartIsAfterAndStatusOrderByStartAsc(
            long itemId, LocalDateTime start, Booking.Status status);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusIsAndEndIsBeforeOrderByEndDesc(
            long userId, long itemId, Booking.Status status, LocalDateTime end);

}
