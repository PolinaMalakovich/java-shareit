package ru.practicum.shareit.server.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
    @Sql(value = {"comment-repository-test.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
    @Sql(value = {"comment-repository-test.after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
class CommentRepositoryTest {
    private final CommentRepository commentRepository;
    private final User alice = new User(1L, "Alice", "alice.anderson@example.com");
    private final User john = new User(2L, "John", "john.doe@example.com");
    private final Item coffeeTable = new Item(3L, "Coffee table", "Old wooden coffee table", true, alice, null);
    private final Comment comment = new Comment(
        4L,
        "Nice!",
        coffeeTable,
        john,
        LocalDateTime.of(2023, 10, 16, 16, 47, 0)
    );

    @Test
    void findCommentsByItem_Id() {
        Stream<Comment> result = commentRepository.findCommentsByItem_Id(coffeeTable.getId());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(comment));
    }
}