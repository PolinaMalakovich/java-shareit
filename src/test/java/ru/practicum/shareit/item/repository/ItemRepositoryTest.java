package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
    @Sql(value = {"item-repository-test.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = {"item-repository-test.after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final User alice = new User(1L, "Alice", "alice.anderson@example.com");
    private final User john = new User(2L, "John", "john.doe@example.com");
    private final ItemRequest tableRequest = new ItemRequest(
        4L,
        "Vintage coffee table, preferably wooden",
        john,
        LocalDateTime.of(2022, 10, 12, 16, 47, 0)
    );
    private final Item coffeeTable = new Item(3L, "Coffee table", "Old wooden coffee table", true, alice, tableRequest);

    @Test
    void findByOwnerIdOrderById() {
        Stream<Item> result = itemRepository.findByOwnerIdOrderById(alice.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(coffeeTable));
    }

    @Test
    void findByRequestIdOrderById() {
        Stream<Item> result = itemRepository.findByRequestIdOrderById(tableRequest.getId());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(coffeeTable));
    }

    @Test
    void searchItem() {
        Stream<Item> result = itemRepository.searchItem("table", Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(coffeeTable));
    }
}