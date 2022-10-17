package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(long id, ItemRequestDto itemRequestDto);

    List<ItemRequestDtoWithAnswers> getRequests(long id);

    List<ItemRequestDtoWithAnswers> getAllRequests(long id, int from, int size);

    ItemRequestDtoWithAnswers getRequest(long id, long requestId);
}
