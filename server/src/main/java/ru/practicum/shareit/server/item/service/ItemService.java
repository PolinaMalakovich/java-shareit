package ru.practicum.shareit.server.item.service;

import ru.practicum.shareit.dto.item.*;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long id, ItemDto itemDto);

    ItemDto getItem(long id);

    ItemDtoWithCommentsAndBookings getItemWithCommentsAndBookings(long id, long itemId);

    List<ListItemDto> getItems(long id, int from, int size);

    ItemDto updateItem(long id, long itemId, PatchItemDto patchItemDto);

    void deleteItem(long id, long itemId);

    List<ItemDto> searchItem(String text, int from, int size);

    CommentDto addComment(long id, long itemId, CommentDto commentDto);
}
