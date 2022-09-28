package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long id, ItemDto itemDto);
    ItemDto getItem(long id);
    ItemDtoWithCommentsAndBookings getItemWithCommentsAndBookings(long id, long itemId);
    List<ListItemDto> getItems(long id);
    ItemDto updateItem(long id, long itemId, PatchItemDto patchItemDto);
    void deleteItem(long id, long itemId);
    List<ItemDto> searchItem(String text);
    CommentDto addComment(long id, long itemId, CommentDto commentDto);
}
