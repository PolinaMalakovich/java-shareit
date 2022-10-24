package ru.practicum.shareit.server.item.service;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.dto.item.*;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public static CommentDto toCommentDto(final Comment comment) {
        return new CommentDto(
            comment.getId(),
            comment.getText(),
            comment.getItem().getId(),
            comment.getAuthor().getId(),
            comment.getAuthor().getName(),
            comment.getCreated()
        );
    }

    public static Comment toComment(final CommentDto commentDto, final Item item, final User user) {
        return new Comment(
            commentDto.getId(),
            commentDto.getText(),
            item,
            user,
            commentDto.getCreated() == null ? LocalDateTime.now() : commentDto.getCreated()
        );
    }
}
