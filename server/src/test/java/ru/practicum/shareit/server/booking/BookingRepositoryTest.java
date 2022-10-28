package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.dto.booking.Status;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
    @Sql(value = {"booking-repository-test.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = {"booking-repository-test.after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final User alice = new User(1L, "Alice", "alice.anderson@example.com");
    private final User john = new User(2L, "John", "john.doe@example.com");
    private final Item coffeeTable = new Item(3L, "Coffee table", "Old wooden coffee table", true, alice, null);
    private final Booking booking = new Booking(
        4L,
        LocalDateTime.of(2023, 10, 13, 16, 47, 0),
        LocalDateTime.of(2023, 10, 15, 16, 47, 0),
        coffeeTable,
        john,
        Status.WAITING);
    private final Item tuxedo = new Item(5L, "Tuxedo", "Notch lapel modern fit tuxedo, black.", true, john, null);
    private final Booking bookingCurrent = new Booking(
        7L,
        LocalDateTime.of(2022, 10, 13, 16, 47, 0),
        LocalDateTime.of(2022, 12, 13, 16, 47, 0),
        tuxedo,
        alice,
        Status.WAITING);
    private final Booking bookingPast = new Booking(
        6L,
        LocalDateTime.of(2021, 10, 13, 16, 47, 0),
        LocalDateTime.of(2021, 10, 15, 16, 47, 0),
        tuxedo,
        alice,
        Status.WAITING);

    @Test
    void findBookingsByBooker_Id() {
        Stream<Booking> result = bookingRepository.findBookingsByBooker_Id(john.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter() {
        Stream<Booking> result = bookingRepository.findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(
            john.getId(),
            LocalDateTime.of(2023, 10, 14, 16, 47, 0),
            LocalDateTime.of(2023, 10, 14, 16, 47, 0),
            Pageable.unpaged()
        );

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void findBookingsByBooker_IdAndEndIsBefore() {
        Stream<Booking> result = bookingRepository.findBookingsByBooker_IdAndEndIsBefore(
            john.getId(),
            LocalDateTime.of(2023, 10, 16, 16, 47, 0),
            Pageable.unpaged()
        );

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void findBookingsByBooker_IdAndStartIsAfter() {
        Stream<Booking> result = bookingRepository.findBookingsByBooker_IdAndStartIsAfter(
            john.getId(),
            LocalDateTime.of(2023, 10, 12, 16, 47, 0),
            Pageable.unpaged()
        );

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void findBookingsByBooker_IdAndStatus() {
        Stream<Booking> result = bookingRepository.findBookingsByBooker_IdAndStatus(
            john.getId(),
            Status.WAITING,
            Pageable.unpaged()
        );

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void getBookingsByUserItems() {
        Stream<Booking> result = bookingRepository.getBookingsByUserItems(alice.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void getBookingsByUserItemsCurrent() {
        Stream<Booking> result = bookingRepository.getBookingsByUserItemsCurrent(john.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(bookingCurrent));
    }

    @Test
    void getBookingsByUserItemsPast() {
        Stream<Booking> result = bookingRepository.getBookingsByUserItemsPast(john.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(bookingPast));
    }

    @Test
    void getBookingsByUserItemsFuture() {
        Stream<Booking> result = bookingRepository.getBookingsByUserItemsFuture(alice.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void getBookingsByUserItemsWithState() {
        Stream<Booking> result = bookingRepository.getBookingsByUserItemsWithState(
            alice.getId(),
            Status.WAITING,
            Pageable.unpaged()
        );

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }

    @Test
    void getPastOrCurrentBookingByItemId() {
        Optional<Booking> result = bookingRepository.getPastOrCurrentBookingByItemId(tuxedo.getId());

        assertThat(result).isNotEmpty().isEqualTo(Optional.of(bookingCurrent));
    }

    @Test
    void getFutureBookingByItemId() {
        Optional<Booking> result = bookingRepository.getFutureBookingByItemId(coffeeTable.getId());

        assertThat(result).isPresent().isEqualTo(Optional.of(booking));
    }

    @Test
    void findBookingsByItem_IdAndBooker_IdAndEndIsBefore() {
        Stream<Booking> result = bookingRepository.findBookingsByItem_IdAndBooker_IdAndEndIsBefore(
            coffeeTable.getId(),
            john.getId(),
            LocalDateTime.of(2023, 10, 16, 16, 47, 0)
        );

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(booking));
    }
}