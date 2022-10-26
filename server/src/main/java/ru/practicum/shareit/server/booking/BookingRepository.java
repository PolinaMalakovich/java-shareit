package ru.practicum.shareit.server.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.dto.booking.Status;
import ru.practicum.shareit.server.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // all
    Stream<Booking> findBookingsByBooker_Id(long id, Pageable pageable);

    // current
    Stream<Booking> findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(long id,
                                                                         LocalDateTime checkStart,
                                                                         LocalDateTime checkEnd,
                                                                         Pageable pageable);

    // past
    Stream<Booking> findBookingsByBooker_IdAndEndIsBefore(long id, LocalDateTime dateTime, Pageable pageable);

    // future
    Stream<Booking> findBookingsByBooker_IdAndStartIsAfter(long id, LocalDateTime dateTime, Pageable pageable);

    // waiting, rejected
    Stream<Booking> findBookingsByBooker_IdAndStatus(long id, Status status, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.owner.id = :id")
    Stream<Booking> getBookingsByUserItems(long id, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b " +
        "WHERE b.item.owner.id = :id " +
        "AND b.start < current_timestamp AND b.end > current_timestamp ")
    Stream<Booking> getBookingsByUserItemsCurrent(long id, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.owner.id = :id AND b.end < current_timestamp")
    Stream<Booking> getBookingsByUserItemsPast(long id, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b WHERE b.item.owner.id = :id AND b.start > current_date")
    Stream<Booking> getBookingsByUserItemsFuture(long id, Pageable pageable);

    @Query(value = "SELECT b FROM Booking AS b " +
        "WHERE b.item.owner.id = :id AND b.status = :status")
    Stream<Booking> getBookingsByUserItemsWithState(long id, Status status, Pageable pageable);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? AND start_date < now() ORDER BY end_date DESC LIMIT 1",
        nativeQuery = true)
    Optional<Booking> getPastOrCurrentBookingByItemId(long id);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? AND start_date > now() ORDER BY start_date LIMIT 1",
        nativeQuery = true)
    Optional<Booking> getFutureBookingByItemId(long id);

    Stream<Booking> findBookingsByItem_IdAndBooker_IdAndEndIsBefore(long itemId, long bookerId, LocalDateTime dateTime);
}
