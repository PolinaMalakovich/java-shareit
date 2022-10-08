package ru.practicum.shareit.request;

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
    @Sql(value = {"request-repository-test.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = {"request-repository-test.after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
class RequestRepositoryTest {
    private final RequestRepository requestRepository;
    private final User alice = new User(1L, "Alice", "alice.anderson@example.com");
    private final User john = new User(2L, "John", "john.doe@example.com");
    private final ItemRequest tableRequest = new ItemRequest(
        4L,
        "Vintage coffee table, preferably wooden",
        john,
        LocalDateTime.of(2022, 10, 12, 16, 47, 0)
    );
    private final Item coffeeTable = new Item(3L, "Coffee table", "Old wooden coffee table", true, alice, tableRequest);
    private final ItemRequest tuxedoRequest = new ItemRequest(
        6L,
        "Black tuxedo",
        alice,
        LocalDateTime.of(2022, 10, 10, 15, 27, 0));
    private final Item tuxedo = new Item(
        5L,
        "Tuxedo",
        "Notch lapel modern fit tuxedo, black.",
        true,
        john,
        tuxedoRequest
    );


    @Test
    void findByRequesterIdOrderByCreatedDesc() {
        Stream<ItemRequest> result = requestRepository.findByRequesterIdOrderByCreatedDesc(alice.getId());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(tuxedoRequest));
    }

    @Test
    void findByRequesterIdNot() {
        Stream<ItemRequest> result = requestRepository.findByRequesterIdNot(alice.getId(), Pageable.unpaged());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(tableRequest));
    }
}