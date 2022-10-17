package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.stream.Stream;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Stream<Item> findByOwnerIdOrderById(long id, Pageable pageable);

    Stream<Item> findByRequestIdOrderById(long id);

    @Query(value = "SELECT i FROM Item AS i " +
        "WHERE i.available IS TRUE AND :text <> '' " +
        "AND (upper(i.name) LIKE concat('%', upper(:text), '%') " +
        "OR upper(i.description) LIKE concat('%', upper(:text), '%'))")
    Stream<Item> searchItem(@Param("text") String text, Pageable pageable);
}
