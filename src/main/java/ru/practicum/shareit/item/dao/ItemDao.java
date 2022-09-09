package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Optional;
import java.util.stream.Stream;

public interface ItemDao {
    Item addItem(Item item);
    Optional<Item> getItem(long id);
    Stream<Item> getItems(long id);
    Optional<Item> updateItem(Item item);
    Optional<Item> deleteItem(long id);
    Stream<Item> searchItem(String text);
}
