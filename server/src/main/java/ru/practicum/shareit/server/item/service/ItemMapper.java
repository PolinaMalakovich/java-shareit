package ru.practicum.shareit.server.item.service;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.dto.booking.BookingDtoBookerId;
import ru.practicum.shareit.dto.item.*;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.service.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
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

    public static ItemDtoForRequests toItemDtoForRequests(final Item item) {
        return new ItemDtoForRequests(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.isAvailable(),
            item.getRequest().getId()
        );
    }

    public static List<ItemDtoForRequests> toItemDtoForRequestsList(final List<Item> items) {
        return items.stream().map(ItemMapper::toItemDtoForRequests).collect(Collectors.toList());
    }
}
