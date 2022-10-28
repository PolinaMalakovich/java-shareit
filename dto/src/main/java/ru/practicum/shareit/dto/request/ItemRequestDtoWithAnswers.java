package ru.practicum.shareit.dto.request;

import lombok.Value;
import ru.practicum.shareit.dto.item.ItemDtoForRequests;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class ItemRequestDtoWithAnswers {
    long id;
    String description;
    LocalDateTime created;
    List<ItemDtoForRequests> items;
}
