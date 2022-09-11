package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ListItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.service.ItemRequestMapper;
import ru.practicum.shareit.user.service.UserMapper;

public class ItemMapper {
    public static ItemDto toItemDto(final Item item) {
        return new ItemDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.isAvailable(),
            UserMapper.toUserDto(item.getOwner()),
            item.getRequest() == null ? null : ItemRequestMapper.toItemRequestDto(item.getRequest())
        );
    }

    public static Item toItem(final ItemDto itemDto) {
        return new Item(
            itemDto.getId(),
            itemDto.getName(),
            itemDto.getDescription(),
            itemDto.isAvailable(),
            UserMapper.toUser(itemDto.getOwner()),
            itemDto.getRequest() == null ? null : ItemRequestMapper.toItemRequest(itemDto.getRequest())
        );
    }

    public static ListItemDto toListItemDto(Item item) {
        return new ListItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
    }
}
