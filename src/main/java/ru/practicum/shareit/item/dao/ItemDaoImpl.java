package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.ListUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static ru.practicum.shareit.util.ListUtils.mutableListOf;

@Component
public class ItemDaoImpl implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Long>> ownerItems = new HashMap<>();
    private long id = 1;

    @Override
    public Item addItem(final Item item) {
        final Item newItem = item.withId(id++);
        items.put(newItem.getId(), newItem);
        ownerItems.merge(newItem.getOwner().getId(), mutableListOf(newItem.getId()), ListUtils::concat);

        return newItem;
    }

    @Override
    public Optional<Item> getItem(final long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Stream<Item> getItems(final long id) {
        return ownerItems.getOrDefault(id, List.of()).stream().map(items::get);
    }

    private Stream<Item> getItems() { return items.values().stream(); }

    @Override
    public Optional<Item> updateItem(final Item item) {
        return getItem(item.getId())
                .map(i -> {
                    items.replace(item.getId(), item);
                    return item;
                });
    }

    @Override
    public Optional<Item> deleteItem(final long id) {
        ownerItems.get(items.get(id).getOwner().getId()).remove(id);

        return Optional.ofNullable(items.remove(id));
    }

    @Override
    public Stream<Item> searchItem(final String text) {
        final String line = text.toLowerCase().trim();

        return line.isEmpty() ? Stream.empty() : getItems().filter(item -> isAvailableAndHasText(item, line));
    }

    private static boolean isAvailableAndHasText(final Item item, final String line) {
        final String name = item.getName().toLowerCase();
        final String description = item.getDescription().toLowerCase();

        return item.isAvailable() && (name.contains(line) || description.contains(line));
    }
}
