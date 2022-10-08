package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(final ItemRequest itemRequest) {
        return new ItemRequestDto(
            itemRequest.getId(),
            itemRequest.getDescription(),
            UserMapper.toUserDto(itemRequest.getRequester()),
            itemRequest.getCreated());
    }

    public static ItemRequest toItemRequest(final ItemRequestDto itemRequestDto) {
        return new ItemRequest(
            itemRequestDto.getId(),
            itemRequestDto.getDescription(),
            UserMapper.toUser(itemRequestDto.getRequester()),
            itemRequestDto.getCreated() == null ? LocalDateTime.now() : itemRequestDto.getCreated()
        );
    }

    public static ItemRequestDtoWithAnswers toItemRequestDtoWithAnswers(final ItemRequest itemRequest,
                                                                        final List<ItemDtoForRequests> items) {
        return new ItemRequestDtoWithAnswers(
            itemRequest.getId(),
            itemRequest.getDescription(),
            itemRequest.getCreated(),
            items
        );
    }
}
