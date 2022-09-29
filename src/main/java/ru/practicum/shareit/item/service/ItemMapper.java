package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoBookerId;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithCommentsAndBookings;
import ru.practicum.shareit.item.dto.ListItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserMapper;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(final Item item) {
        return new ItemDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.isAvailable(),
            UserMapper.toUserDto(item.getOwner()),
            item.getRequest() == null ? null : item.getRequest().getId()
        );
    }

    public static ItemDtoWithCommentsAndBookings toItemDtoWithCommentsAndBookings(final Item item,
                                                                                  final List<CommentDto> comments,
                                                                                  final BookingDtoBookerId lastBooking,
                                                                                  final BookingDtoBookerId nextBooking) {
        return new ItemDtoWithCommentsAndBookings(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.isAvailable(),
            UserMapper.toUserDto(item.getOwner()),
            item.getRequest() == null ? null : item.getRequest().getId(),
            comments,
            lastBooking,
            nextBooking
        );
    }

    public static Item toItem(final ItemDto itemDto, final ItemRequest itemRequest) {
        return new Item(
            itemDto.getId(),
            itemDto.getName(),
            itemDto.getDescription(),
            itemDto.isAvailable(),
            UserMapper.toUser(itemDto.getOwner()),
            itemRequest
        );
    }

    public static ListItemDto toListItemDto(final Item item,
                                            final List<CommentDto> comments,
                                            final BookingDtoBookerId lastBooking,
                                            final BookingDtoBookerId nextBooking) {
        return new ListItemDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.isAvailable(),
            comments,
            lastBooking,
            nextBooking
        );
    }
}
