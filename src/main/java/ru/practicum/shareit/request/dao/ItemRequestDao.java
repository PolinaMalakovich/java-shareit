package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Optional;

public interface ItemRequestDao {
    ItemRequest addItemRequest(ItemRequest itemRequest);

    Optional<ItemRequest> getItemRequest(long id);

    Optional<ItemRequest> updateItemRequest(ItemRequest itemRequest);

    Optional<ItemRequest> deleteItemRequest(long id);
}
