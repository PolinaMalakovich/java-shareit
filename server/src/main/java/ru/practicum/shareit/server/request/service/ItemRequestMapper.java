package ru.practicum.shareit.server.request.service;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.dto.request.*;
import ru.practicum.shareit.dto.item.*;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.service.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
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
