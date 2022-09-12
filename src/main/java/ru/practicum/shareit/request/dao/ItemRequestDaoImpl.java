package ru.practicum.shareit.request.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ItemRequestDaoImpl implements ItemRequestDao {
    private final Map<Long, ItemRequest> itemRequests = new HashMap<>();
    private long id = 1;

    @Override
    public ItemRequest addItemRequest(final ItemRequest itemRequest) {
        final ItemRequest newItemRequest = itemRequest.withId(id++);
        itemRequests.put(newItemRequest.getId(), newItemRequest);

        return newItemRequest;
    }

    @Override
    public Optional<ItemRequest> getItemRequest(final long id) {
        return Optional.ofNullable(itemRequests.get(id));
    }

    @Override
    public Optional<ItemRequest> updateItemRequest(final ItemRequest itemRequest) {
        return getItemRequest(itemRequest.getId())
            .map(i -> {
                itemRequests.replace(itemRequest.getId(), itemRequest);
                return itemRequest;
            });
    }

    @Override
    public Optional<ItemRequest> deleteItemRequest(final long id) {
        return Optional.ofNullable(itemRequests.remove(id));
    }
}
