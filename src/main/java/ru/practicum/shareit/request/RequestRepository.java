package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.stream.Stream;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    Stream<ItemRequest> findByRequesterIdOrderByCreatedDesc(long id);

    Stream<ItemRequest> findByRequesterIdNot(long id, Pageable pageable);
}
