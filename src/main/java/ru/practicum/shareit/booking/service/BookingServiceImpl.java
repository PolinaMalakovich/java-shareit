package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.shareit.booking.service.BookingMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addBooking(final long id, final NewBookingDto newBookingDto) {
        final User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User", id));
        final Item item = itemRepository.findById(newBookingDto.getItemId())
            .orElseThrow(() -> new EntityNotFoundException("Item", newBookingDto.getItemId()));
        if (!item.isAvailable()) {
            throw new ItemUnavailableException(item.getId());
        }
        if (Objects.equals(user.getId(), item.getOwner().getId())) {
            throw new ForbiddenException(user.getId(), item.getId(), "Item", "book");
        }
        final Booking newBooking = bookingRepository.save(toBooking(newBookingDto, item, user));
        log.info("New booking created successfully.");

        return toBookingDto(newBooking);
    }

    @Override
    public BookingDto getBooking(final long id, final long bookingId) {
        final Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new EntityNotFoundException("Booking", bookingId));
        if (id != booking.getBooker().getId() && id != booking.getItem().getOwner().getId()) {
            throw new ForbiddenException(id, bookingId, "Booking", "access");
        }

        return toBookingDto(booking);
    }

    @Override
    @Transactional
    public List<BookingDto> getUserBookings(final long id, final State state) {
        userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        final Stream<Booking> stream;

        switch (state) {
            case WAITING:
                stream = bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(id, Status.WAITING);
                break;
            case REJECTED:
                stream = bookingRepository.findBookingsByBooker_IdAndStatusOrderByStartDesc(id, Status.REJECTED);
                break;
            case CURRENT:
                stream = bookingRepository.findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(
                    id,
                    LocalDateTime.now(),
                    LocalDateTime.now()
                );
                break;
            case PAST:
                stream =
                    bookingRepository.findBookingsByBooker_IdAndEndIsBeforeOrderByStartDesc(id, LocalDateTime.now());
                break;
            case FUTURE:
                stream =
                    bookingRepository.findBookingsByBooker_IdAndStartIsAfterOrderByStartDesc(id, LocalDateTime.now());
                break;
            case ALL:
            default:
                stream = bookingRepository.findBookingsByBooker_IdOrderByStartDesc(id);
                break;
        }

        return stream.map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingDto> getBookingsByUserItems(final long id, State state) {
        userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        final Stream<Booking> stream;

        switch (state) {
            case WAITING:
                stream = bookingRepository.getBookingsByUserItemsWithState(id, Status.WAITING);
                break;
            case REJECTED:
                stream = bookingRepository.getBookingsByUserItemsWithState(id, Status.REJECTED);
                break;
            case CURRENT:
                stream = bookingRepository.getBookingsByUserItemsCurrent(id);
                break;
            case PAST:
                stream = bookingRepository.getBookingsByUserItemsPast(id);
                break;
            case FUTURE:
                stream = bookingRepository.getBookingsByUserItemsFuture(id);
                break;
            case ALL:
            default:
                stream = bookingRepository.getBookingsByUserItems(id);
                break;
        }

        return stream.map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public BookingDto approveOrRejectBooking(final long id, final long bookingId, final boolean approved) {
        final Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new EntityNotFoundException("Booking", bookingId));
        if (id != booking.getItem().getOwner().getId()) {
            throw new ForbiddenException(id, bookingId, "Booking", "access");
        }
        Status status = approved ? Status.APPROVED : Status.REJECTED;
        if (booking.getStatus() == status) {
            throw new IllegalArgumentException();
        }
        booking.setStatus(status);

        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public void deleteBooking(long id) {
        throw new UnsupportedOperationException();
    }
}
