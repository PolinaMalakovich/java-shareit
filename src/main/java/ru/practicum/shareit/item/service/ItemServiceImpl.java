package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoBookerId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.UnauthorizedCommentException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.shareit.item.service.CommentMapper.toComment;
import static ru.practicum.shareit.item.service.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.service.ItemMapper.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addItem(final long id, final ItemDto itemDto) {
        final UserDto owner = UserMapper.toUserDto(
            userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id))
        );
        final Long itemRequestId = itemDto.getRequest();
        ItemRequest itemRequest = null;
        if (itemRequestId != null) {
            itemRequest = requestRepository.findById(itemRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Item request", id));
        }
        final Item newItem = itemRepository.save(
            toItem(itemDto.withOwner(owner), itemRequest)
        );
        log.info("New item created successfully.");

        return toItemDto(newItem);
    }

    @Override
    public ItemDto getItem(final long id) {
        final Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item", id));

        return toItemDto(item);
    }

    @Override
    public ItemDtoWithCommentsAndBookings getItemWithCommentsAndBookings(final long id, final long itemId) {
        final Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new EntityNotFoundException("Item", itemId));

        BookingDtoBookerId pastOrCurrentBooking;
        BookingDtoBookerId futureBooking;
        if (id == item.getOwner().getId()) {
            pastOrCurrentBooking = bookingRepository.getPastOrCurrentBookingByItemId(item.getId())
                .map(BookingMapper::toBookingDtoBookerId)
                .orElse(null);
            futureBooking = bookingRepository.getFutureBookingByItemId(item.getId())
                .map(BookingMapper::toBookingDtoBookerId)
                .orElse(null);
        } else {
            pastOrCurrentBooking = null;
            futureBooking = null;
        }

        return ItemMapper.toItemDtoWithCommentsAndBookings(
            item,
            commentRepository
                .findCommentsByItem_Id(item.getId())
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()),
            pastOrCurrentBooking,
            futureBooking
        );
    }

    @Override
    @Transactional
    public List<ListItemDto> getItems(final long id) {
        return itemRepository.findByOwnerIdOrderById(id)
            .map(item -> toListItemDto(
                item,
                commentRepository
                    .findCommentsByItem_Id(item.getId())
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()),
                bookingRepository.getPastOrCurrentBookingByItemId(item.getId())
                    .map(BookingMapper::toBookingDtoBookerId)
                    .orElse(null),
                bookingRepository.getFutureBookingByItemId(item.getId())
                    .map(BookingMapper::toBookingDtoBookerId)
                    .orElse(null)
            )).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto updateItem(final long id, final long itemId, final PatchItemDto patchItemDto) {
        final Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new EntityNotFoundException("Item", itemId));
        if (item.getOwner().getId() != id) {
            throw new ForbiddenException(id, itemId, "item", "edit");
        }
        if (patchItemDto.getName() != null) {
            item.setName(patchItemDto.getName());
        }
        if (patchItemDto.getDescription() != null) {
            item.setDescription(patchItemDto.getDescription());
        }
        if (patchItemDto.getAvailable() != null) {
            item.setAvailable(patchItemDto.getAvailable());
        }
        log.info("Item " + item.getId() + " updated successfully.");

        return toItemDto(item);
    }

    @Override
    @Transactional
    public void deleteItem(long id, long itemId) {
        final ItemDto itemDto = getItem(itemId);
        if (itemDto.getOwner().getId() != id) {
            throw new ForbiddenException(id, itemId, "item", "delete");
        }
        itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item", id));
        itemRepository.deleteById(id);
        log.info("Item " + id + " deleted successfully.");
    }

    @Override
    @Transactional
    public List<ItemDto> searchItem(final String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchItem(text).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(long id, long itemId, CommentDto commentDto) {
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Item", id));
        final User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        final Stream<Booking> bookings = bookingRepository
            .findBookingsByItem_IdAndBooker_IdAndEndIsBefore(itemId, id, LocalDateTime.now());
        if (bookings.findAny().isEmpty()) {
            throw new UnauthorizedCommentException(id, itemId);
        }
        final Comment comment = toComment(commentDto, item, user);
        commentRepository.save(comment);
        log.info("New comment created successfully.");

        return toCommentDto(comment);
    }
}
