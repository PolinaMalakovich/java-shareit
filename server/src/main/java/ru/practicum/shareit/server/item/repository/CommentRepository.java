package ru.practicum.shareit.server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.server.item.model.Comment;

import java.util.stream.Stream;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Stream<Comment> findCommentsByItem_Id(long id);
}
