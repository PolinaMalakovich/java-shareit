package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ListItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;

import java.util.stream.Stream;

public interface ItemService {
    ItemDto addItem(long id, ItemDto itemDto);
    ItemDto getItem(long id);
    Stream<ListItemDto> getItems(long id);
    ItemDto updateItem(long id, long itemId, PatchItemDto patchItemDto);
    void deleteItem(long id, long itemId);
    Stream<ItemDto> searchItem(String text);
}
