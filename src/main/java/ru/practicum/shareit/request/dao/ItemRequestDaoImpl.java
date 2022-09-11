package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemRequestDaoImpl implements ItemRequestDao {
    private final Map<Long, ItemRequest> itemRequests = new HashMap<>();
    private final long id = 1;

    @Override
    public ItemRequest addItemRequest(final ItemRequest itemRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ItemRequest> getItemRequest(final long id) {
        return Optional.ofNullable(itemRequests.get(id));
    }

    @Override
    public Optional<ItemRequest> updateItemRequest(final ItemRequest itemRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ItemRequest> deleteItemRequest(final long id) {
        throw new UnsupportedOperationException();
    }
}
