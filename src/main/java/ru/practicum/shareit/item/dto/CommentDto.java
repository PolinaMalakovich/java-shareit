package ru.practicum.shareit.item.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class CommentDto {
    long id;
    String text;
    long itemId;
    long authorId;
    String authorName;
    LocalDateTime created;
}
