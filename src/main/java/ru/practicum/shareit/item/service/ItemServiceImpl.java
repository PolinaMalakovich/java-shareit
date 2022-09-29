package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.shareit.item.service.CommentMapper.toComment;
import static ru.practicum.shareit.item.service.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.service.ItemMapper.toItem;
import static ru.practicum.shareit.item.service.ItemMapper.toListItemDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
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

        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto getItem(final long id) {
        final Item item = itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item", id));

        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
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
    public ItemDto updateItem(final long id, final long itemId, final PatchItemDto patchItemDto) {
        final ItemDto itemDto = getItem(itemId);
        if (itemDto.getOwner().getId() != id) {
            throw new ForbiddenException(id, itemId, "item", "edit");
        }
        final ItemDto newItemDto = patchItemDto.patch(itemDto);
        final Long itemRequestId = newItemDto.getRequest();
        ItemRequest itemRequest = null;
        if (itemRequestId != null) {
            itemRequest = requestRepository.findById(itemRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Item request", id));
        }
        itemRepository.findById(newItemDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("Item", newItemDto.getId()));
        itemRepository.save(toItem(newItemDto, itemRequest));
        log.info("Item " + newItemDto.getId() + " updated successfully.");

        return newItemDto;
    }

    @Override
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
        return itemRepository.searchItem(text).map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(long id, long itemId, CommentDto commentDto) {
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Item", id));
        final User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
        final Stream<Booking> bookings = bookingRepository.findBookingsByItem_IdAndBooker_Id(itemId, id)
            .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()));
        if (bookings.findAny().isEmpty()) {
            throw new UnauthorizedCommentException(id, itemId);
        }
        final Comment comment = toComment(commentDto, item, user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);
        log.info("New comment created successfully.");

        return toCommentDto(comment);
    }
}
