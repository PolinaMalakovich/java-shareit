package ru.practicum.shareit.server.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.dto.booking.*;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.dto.booking.State;
import ru.practicum.shareit.dto.booking.Status;
import ru.practicum.shareit.server.exception.EntityNotFoundException;
import ru.practicum.shareit.server.exception.ForbiddenException;
import ru.practicum.shareit.server.exception.ItemUnavailableException;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    BookingRepository mockBookingRepository;
    @Mock
    UserRepository mockUserRepository;
    @Mock
    ItemRepository mockItemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void addBookingWhenUserDoesNotExist() {
        long userId = 42;
        NewBookingDto newBookingDto = new NewBookingDto(LocalDateTime.now(), LocalDateTime.now().plusDays(4), 2);
        Mockito.when(mockUserRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> bookingService.addBooking(userId, newBookingDto),
            "Expected addBooking() to throw EntityNotFoundException because the user does not exist.");
    }

    @Test
    void addBookingWhenItemDoesNotExist() {
        long itemId = 42;
        User user = new User(1L, "John", "john.doe@example.com");
        NewBookingDto newBookingDto = new NewBookingDto(LocalDateTime.now(), LocalDateTime.now().plusDays(4), itemId);
        Mockito.when(mockUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(mockItemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> bookingService.addBooking(user.getId(), newBookingDto),
            "Expected addBooking() to throw EntityNotFoundException because the item does not exist."
        );
    }

    @Test
    void addBookingWhenItemIsNotAvailable() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(3L, "Coffee table", "Antique wooden coffee table", false, alice, null);
        NewBookingDto newBookingDto = new NewBookingDto(
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(4),
            item.getId()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito.when(mockItemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(
            ItemUnavailableException.class,
            () -> bookingService.addBooking(john.getId(), newBookingDto),
            "Expected addBooking() to throw ItemUnavailableException because the item is not available."
        );
    }

    @Test
    void addBookingWhenBookerIsOwner() {
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(2L, "Coffee table", "Antique wooden coffee table", true, alice, null);
        NewBookingDto newBookingDto = new NewBookingDto(
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(4),
            item.getId()
        );
        Mockito.when(mockUserRepository.findById(alice.getId())).thenReturn(Optional.of(alice));
        Mockito.when(mockItemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(
            ForbiddenException.class,
            () -> bookingService.addBooking(alice.getId(), newBookingDto),
            "Expected addBooking() to throw ForbiddenException because the owner cannot book their item."
        );
    }

    @Test
    void addBooking() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(3L, "Coffee table", "Antique wooden coffee table", true, alice, null);
        NewBookingDto newBookingDto = new NewBookingDto(
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(4),
            item.getId()
        );
        Booking booking = new Booking(
            4L,
            newBookingDto.getStart(),
            newBookingDto.getEnd(),
            item,
            john,
            Status.WAITING
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(item.getId(), item.getName()),
            new BookingDto.Booker(john.getId(), john.getName()),
            Status.WAITING
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito.when(mockItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(mockBookingRepository.save(any())).thenReturn(booking);
        BookingDto actual = bookingService.addBooking(john.getId(), newBookingDto);

        assertEquals(expected, actual);
    }

    @Test
    void getBookingWhenBookingDoesNotExist() {
        long userId = 1;
        long bookingId = 42;
        Mockito.when(mockBookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> bookingService.getBooking(userId, bookingId),
            "Expected getBooking() to throw EntityNotFoundException because the booking does not exist."
        );
    }

    @Test
    void getBookingWhenUserIsBooker() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            item,
            john,
            Status.WAITING
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockBookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingDto actual = bookingService.getBooking(john.getId(), booking.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getBookingWhenUserIsOwner() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            item,
            john,
            Status.WAITING
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockBookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingDto actual = bookingService.getBooking(alice.getId(), booking.getId());

        assertEquals(expected, actual);
    }

    @Test
    void getBookingWhenUserIsNeitherBookerNorOwner() {
        long randomId = 42;
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            item,
            john,
            Status.WAITING
        );
        Mockito.when(mockBookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.getBooking(randomId, booking.getId()));
    }

    @Test
    void getUserBookingsWhenUserDoesNotExist() {
        long randomId = 42;
        Mockito.when(mockUserRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> bookingService.getUserBookings(randomId, State.ALL, 0, 10)
        );
    }

    @Test
    void getUserBookingsWhenStateIsWaiting() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            item,
            john,
            Status.WAITING
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(
                mockBookingRepository.findBookingsByBooker_IdAndStatus(
                    john.getId(),
                    Status.WAITING,
                    PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"))
                )
            )
            .thenReturn(Stream.of(booking));
        List<BookingDto> actual = bookingService.getUserBookings(john.getId(), State.WAITING, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getUserBookingsWhenStateIsRejected() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            item,
            john,
            Status.REJECTED
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(
                mockBookingRepository.findBookingsByBooker_IdAndStatus(
                    john.getId(),
                    Status.REJECTED,
                    PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"))
                )
            )
            .thenReturn(Stream.of(booking));
        List<BookingDto> actual = bookingService.getUserBookings(john.getId(), State.REJECTED, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getUserBookingsWhenStateIsCurrent() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().plusDays(2),
            item,
            john,
            Status.APPROVED
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(
                mockBookingRepository.findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(
                    eq(john.getId()),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class),
                    eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                )
            )
            .thenReturn(Stream.of(booking));
        List<BookingDto> actual = bookingService.getUserBookings(john.getId(), State.CURRENT, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getUserBookingsWhenStateIsPast() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now().minusDays(4),
            LocalDateTime.now().minusDays(2),
            item,
            john,
            Status.APPROVED
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(
                mockBookingRepository.findBookingsByBooker_IdAndEndIsBefore(
                    eq(john.getId()),
                    any(LocalDateTime.class),
                    eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                )
            )
            .thenReturn(Stream.of(booking));
        List<BookingDto> actual = bookingService.getUserBookings(john.getId(), State.PAST, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getUserBookingsWhenStateIsFuture() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(4),
            item,
            john,
            Status.APPROVED
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(
                mockBookingRepository.findBookingsByBooker_IdAndStartIsAfter(
                    eq(john.getId()),
                    any(LocalDateTime.class),
                    eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                )
            )
            .thenReturn(Stream.of(booking));
        List<BookingDto> actual = bookingService.getUserBookings(john.getId(), State.FUTURE, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getUserBookingsWhenStateIsAll() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(4),
            item,
            john,
            Status.APPROVED
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(
                mockBookingRepository.findBookingsByBooker_Id(
                    eq(john.getId()),
                    eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                )
            )
            .thenReturn(Stream.of(booking));
        List<BookingDto> actual = bookingService.getUserBookings(john.getId(), State.ALL, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getBookingsByUserItemsWhenUserDoesNotExist() {
        long randomId = 42;
        Mockito.when(mockUserRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> bookingService.getBookingsByUserItems(randomId, State.ALL, 0, 10)
        );
    }

    @Test
    void getBookingsByUserItemsWhenStateIsWaiting() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            item,
            john,
            Status.WAITING
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(
                mockBookingRepository.getBookingsByUserItemsWithState(
                    john.getId(),
                    Status.WAITING,
                    PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"))
                )
            )
            .thenReturn(Stream.of(booking));
        List<BookingDto> actual = bookingService.getBookingsByUserItems(john.getId(), State.WAITING, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getBookingsByUserItemsWhenStateIsRejected() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            item,
            john,
            Status.REJECTED
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(
                mockBookingRepository.getBookingsByUserItemsWithState(
                    john.getId(),
                    Status.REJECTED,
                    PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start"))
                )
            )
            .thenReturn(Stream.of(booking));
        List<BookingDto> actual = bookingService.getBookingsByUserItems(john.getId(), State.REJECTED, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getBookingsByUserItemsWhenStateIsCurrent() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().plusDays(2),
            item,
            john,
            Status.APPROVED
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(
                mockBookingRepository.getBookingsByUserItemsCurrent(
                    eq(john.getId()),
                    eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                )
            )
            .thenReturn(Stream.of(booking));
        List<BookingDto> actual = bookingService.getBookingsByUserItems(john.getId(), State.CURRENT, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getBookingsByUserItemsWhenStateIsPast() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now().minusDays(4),
            LocalDateTime.now().minusDays(2),
            item,
            john,
            Status.APPROVED
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(
                mockBookingRepository.getBookingsByUserItemsPast(
                    eq(john.getId()),
                    eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                )
            )
            .thenReturn(Stream.of(booking));
        List<BookingDto> actual = bookingService.getBookingsByUserItems(john.getId(), State.PAST, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getBookingsByUserItemsWhenStateIsFuture() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(4),
            item,
            john,
            Status.APPROVED
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(
                mockBookingRepository.getBookingsByUserItemsFuture(
                    eq(john.getId()),
                    eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                )
            )
            .thenReturn(Stream.of(booking));
        List<BookingDto> actual = bookingService.getBookingsByUserItems(john.getId(), State.FUTURE, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void getBookingsByUserItemsWhenStateIsAll() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(4),
            item,
            john,
            Status.APPROVED
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            booking.getStatus()
        );
        Mockito.when(mockUserRepository.findById(john.getId())).thenReturn(Optional.of(john));
        Mockito
            .when(
                mockBookingRepository.getBookingsByUserItems(
                    eq(john.getId()),
                    eq(PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "start")))
                )
            )
            .thenReturn(Stream.of(booking));
        List<BookingDto> actual = bookingService.getBookingsByUserItems(john.getId(), State.ALL, 0, 1);

        assertEquals(List.of(expected), actual);
    }

    @Test
    void approveOrRejectBookingWhenBookingDoesNotExist() {
        long bookingId = 42;
        Mockito.when(mockBookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(
            EntityNotFoundException.class,
            () -> bookingService.approveOrRejectBooking(1L, bookingId, true)
        );
    }

    @Test
    void approveOrRejectBookingWhenUserIsNotOwner() {
        long randomId = 42;
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            item,
            john,
            Status.WAITING
        );
        Mockito.when(mockBookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(
            ForbiddenException.class,
            () -> bookingService.approveOrRejectBooking(randomId, booking.getId(), true)
        );
    }

    @Test
    void approveOrRejectBookingWhenStatusIsTheSame() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            item,
            john,
            Status.APPROVED
        );
        Mockito.when(mockBookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(
            IllegalArgumentException.class,
            () -> bookingService.approveOrRejectBooking(alice.getId(), booking.getId(), true)
        );
    }

    @Test
    void approveOrRejectBooking() {
        User john = new User(1L, "John", "john.doe@example.com");
        User alice = new User(2L, "Alice", "alice.anderson@example.com");
        Item item = new Item(
            3L,
            "Coffee table",
            "Antique wooden coffee table",
            true,
            alice,
            null
        );
        Booking booking = new Booking(
            5L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            item,
            john,
            Status.WAITING
        );
        BookingDto expected = new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            new BookingDto.BookedItem(booking.getItem().getId(), booking.getItem().getName()),
            new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()),
            Status.APPROVED
        );
        Mockito.when(mockBookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingDto actual = bookingService.approveOrRejectBooking(alice.getId(), booking.getId(), true);

        assertEquals(expected, actual);
    }
}