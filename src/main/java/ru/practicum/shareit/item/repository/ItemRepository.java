package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.stream.Stream;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Stream<Item> findByOwnerIdOrderById(long id);

    @Query(value = "SELECT * FROM items " +
        "WHERE is_available AND :text <> '' " +
        "AND (name ILIKE '%' || :text || '%' OR description ILIKE '%' || :text || '%')",
        nativeQuery = true)
    Stream<Item> searchItem(@Param("text") String text);
}
