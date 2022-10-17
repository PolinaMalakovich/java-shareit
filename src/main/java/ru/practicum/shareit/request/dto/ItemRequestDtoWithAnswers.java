package ru.practicum.shareit.request.dto;

import lombok.Value;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class ItemRequestDtoWithAnswers {
    long id;
    String description;
    LocalDateTime created;
    List<ItemDtoForRequests> items;
}
