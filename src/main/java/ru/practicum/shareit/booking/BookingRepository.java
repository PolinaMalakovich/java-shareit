package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // all
    Stream<Booking> findBookingsByBooker_IdOrderByStartDesc(long id);

    // current
    Stream<Booking> findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(long id,
                                                                         LocalDateTime checkStart,
                                                                         LocalDateTime checkEnd);

    // past
    Stream<Booking> findBookingsByBooker_IdAndEndIsBeforeOrderByStartDesc(long id, LocalDateTime dateTime);

    // future
    Stream<Booking> findBookingsByBooker_IdAndStartIsAfterOrderByStartDesc(long id, LocalDateTime dateTime);

    // waiting, rejected
    Stream<Booking> findBookingsByBooker_IdAndStatusOrderByStartDesc(long id, Status status);

    @Query(value = "SELECT * FROM bookings " +
        "LEFT JOIN items ON bookings.item_id = items.item_id " +
        "LEFT JOIN users ON items.owner_id = users.user_id " +
        "WHERE owner_id = ? " +
        "ORDER BY start_date DESC",
        nativeQuery = true)
    Stream<Booking> getBookingsByUserItems(long id);

    @Query(value = "SELECT * FROM bookings " +
        "LEFT JOIN items ON bookings.item_id = items.item_id " +
        "LEFT JOIN users ON items.owner_id = users.user_id " +
        "WHERE owner_id = ? AND start_date < now() AND end_date > now()" +
        "ORDER BY start_date DESC",
        nativeQuery = true)
    Stream<Booking> getBookingsByUserItemsCurrent(long id);

    @Query(value = "SELECT * FROM bookings " +
        "LEFT JOIN items ON bookings.item_id = items.item_id " +
        "LEFT JOIN users ON items.owner_id = users.user_id " +
        "WHERE owner_id = ? AND end_date < now()" +
        "ORDER BY start_date DESC",
        nativeQuery = true)
    Stream<Booking> getBookingsByUserItemsPast(long id);

    @Query(value = "SELECT * FROM bookings " +
        "LEFT JOIN items ON bookings.item_id = items.item_id " +
        "LEFT JOIN users ON items.owner_id = users.user_id " +
        "WHERE owner_id = ? AND start_date > now()" +
        "ORDER BY start_date DESC",
        nativeQuery = true)
    Stream<Booking> getBookingsByUserItemsFuture(long id);

    @Query(value = "SELECT b FROM Booking AS b " +
        "WHERE b.item.owner.id = :id AND b.status = :status " +
        "ORDER BY b.start DESC")
    Stream<Booking> getBookingsByUserItemsWithState(long id, Status status);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? AND start_date < now() ORDER BY end_date DESC LIMIT 1",
        nativeQuery = true)
    Optional<Booking> getPastOrCurrentBookingByItemId(long id);

    @Query(value = "SELECT * FROM bookings WHERE item_id = ? AND start_date > now() ORDER BY start_date LIMIT 1",
        nativeQuery = true)
    Optional<Booking> getFutureBookingByItemId(long id);

    Stream<Booking> findBookingsByItem_IdAndBooker_Id(long itemId, long bookerId);
}
