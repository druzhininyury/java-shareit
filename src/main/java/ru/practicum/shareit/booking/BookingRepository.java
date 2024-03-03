package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStartIsAfterAndEndIsBeforeOrderByStartDesc
            (long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, Booking.Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Booking.Status status);

}
