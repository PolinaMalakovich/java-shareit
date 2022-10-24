package ru.practicum.shareit.server.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.BookingRepository;
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
import ru.practicum.shareit.dto.booking.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
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
        final Booking newBooking = bookingRepository.save(BookingMapper.toBooking(newBookingDto, item, user));
        log.info("New booking created successfully.");

        return BookingMapper.toBookingDto(newBooking);
    }

    @Override
    public BookingDto getBooking(final long id, final long bookingId) {
        final Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new EntityNotFoundException("Booking", bookingId));
        if (id != booking.getBooker().getId() && id != booking.getItem().getOwner().getId()) {
            throw new ForbiddenException(id, bookingId, "Booking", "access");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public List<BookingDto> getUserBookings(final long id, final State state, final int from, final int size) {
        userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        final Stream<Booking> stream;

        switch (state) {
            case WAITING:
                stream = bookingRepository.findBookingsByBooker_IdAndStatus(
                    id,
                    Status.WAITING,
                    PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case REJECTED:
                stream = bookingRepository.findBookingsByBooker_IdAndStatus(
                    id,
                    Status.REJECTED,
                    PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case CURRENT:
                stream = bookingRepository.findBookingsByBooker_IdAndStartIsBeforeAndEndIsAfter(
                    id,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case PAST:
                stream =
                    bookingRepository.findBookingsByBooker_IdAndEndIsBefore(
                        id,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                    );
                break;
            case FUTURE:
                stream =
                    bookingRepository.findBookingsByBooker_IdAndStartIsAfter(
                        id,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                    );
                break;
            case ALL:
            default:
                stream = bookingRepository.findBookingsByBooker_Id(
                    id,
                    PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
        }

        return stream.map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<BookingDto> getBookingsByUserItems(final long id, State state, final int from, final int size) {
        userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        final Stream<Booking> stream;

        switch (state) {
            case WAITING:
                stream = bookingRepository.getBookingsByUserItemsWithState(
                    id,
                    Status.WAITING,
                    PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case REJECTED:
                stream = bookingRepository.getBookingsByUserItemsWithState(
                    id,
                    Status.REJECTED,
                    PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case CURRENT:
                stream = bookingRepository.getBookingsByUserItemsCurrent(
                    id, PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case PAST:
                stream = bookingRepository.getBookingsByUserItemsPast(
                    id, PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case FUTURE:
                stream = bookingRepository.getBookingsByUserItemsFuture(
                    id, PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
            case ALL:
            default:
                stream = bookingRepository.getBookingsByUserItems(
                    id, PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"))
                );
                break;
        }

        return stream.map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
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

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public void deleteBooking(long id) {
        throw new UnsupportedOperationException();
    }
}
