package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
        return new Comment(commentDto.getId(), commentDto.getText(), item, user, commentDto.getCreated());
    }
}